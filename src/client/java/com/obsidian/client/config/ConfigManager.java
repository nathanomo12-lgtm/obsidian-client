package com.obsidian.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.obsidian.client.ObsidianClientMod;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles persisting and restoring module enabled-state and keybinds to disk.
 */
public final class ConfigManager {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("obsidian");
	private static final Path CONFIG_FILE = CONFIG_DIR.resolve("obsidian-client.json");

	private ConfigManager() {
	}

	public static void load(ModuleManager moduleManager) {
		if (!Files.exists(CONFIG_FILE)) {
			ObsidianClientMod.LOGGER.info("No existing config found, using defaults.");
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
			ObsidianConfig config = GSON.fromJson(reader, ObsidianConfig.class);
			if (config == null || config.modules == null) {
				return;
			}

			for (Module module : moduleManager.getModules()) {
				ObsidianConfig.ModuleState state = config.modules.get(module.getName());
				if (state == null) {
					continue;
				}

				moduleManager.setEnabled(module, state.enabled);

				if (state.keyCode != -1) {
					module.getKeyBinding().setBoundKey(InputUtil.Type.KEYSYM.createFromCode(state.keyCode));
				}
			}
			KeyBinding.updateKeysByCode();
			ObsidianClientMod.LOGGER.info("Loaded Obsidian Client config from {}", CONFIG_FILE);
		} catch (IOException e) {
			ObsidianClientMod.LOGGER.error("Failed to load Obsidian Client config", e);
		}
	}

	public static void save(ModuleManager moduleManager) {
		ObsidianConfig config = new ObsidianConfig();

		for (Module module : moduleManager.getModules()) {
			ObsidianConfig.ModuleState state = new ObsidianConfig.ModuleState();
			state.enabled = module.isEnabled();
			state.keyCode = KeyBindingHelper.getBoundKeyOf(module.getKeyBinding()).getCode();
			config.modules.put(module.getName(), state);
		}

		try {
			Files.createDirectories(CONFIG_DIR);
			try (Writer writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
				GSON.toJson(config, writer);
			}
			ObsidianClientMod.LOGGER.info("Saved Obsidian Client config to {}", CONFIG_FILE);
		} catch (IOException e) {
			ObsidianClientMod.LOGGER.error("Failed to save Obsidian Client config", e);
		}
	}
}
