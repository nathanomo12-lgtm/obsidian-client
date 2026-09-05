package com.obsidian.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import com.obsidian.client.ObsidianClientMod;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleManager;
import com.obsidian.client.module.Profile;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
			if (config == null) {
				return;
			}

			Profile profile = Profile.fromNameOrDefault(config.activeProfile, Profile.CUSTOM);
			// Set the profile before restoring toggles so the Competitive-profile
			// enforcement in ModuleManager applies while loading, not just afterwards.
			moduleManager.setActiveProfile(profile);

			Map<String, ObsidianConfig.ModuleState> moduleStates = config.profiles == null
					? Collections.emptyMap()
					: config.profiles.getOrDefault(profile.name(), Collections.emptyMap());

			for (Module module : moduleManager.getModules()) {
				ObsidianConfig.ModuleState state = moduleStates.get(module.getName());
				if (state == null) {
					continue;
				}

				moduleManager.setEnabled(module, state.enabled);

				if (state.keyCode != -1) {
					module.getKeyBinding().setKey(InputConstants.Type.KEYSYM.getOrCreate(state.keyCode));
				}
			}
			KeyMapping.resetMapping();
			ObsidianClientMod.LOGGER.info("Loaded Obsidian Client config ({} profile) from {}", profile, CONFIG_FILE);
		} catch (IOException e) {
			ObsidianClientMod.LOGGER.error("Failed to load Obsidian Client config", e);
		}
	}

	public static void save(ModuleManager moduleManager) {
		// Read-modify-write so saving the current profile doesn't clobber the
		// other profiles' saved state in the same file.
		ObsidianConfig config = readExistingConfig();
		Profile activeProfile = moduleManager.getActiveProfile();
		config.activeProfile = activeProfile.name();

		Map<String, ObsidianConfig.ModuleState> moduleStates = new LinkedHashMap<>();
		for (Module module : moduleManager.getModules()) {
			ObsidianConfig.ModuleState state = new ObsidianConfig.ModuleState();
			state.enabled = module.isEnabled();
			state.keyCode = KeyBindingHelper.getBoundKeyOf(module.getKeyBinding()).getValue();
			moduleStates.put(module.getName(), state);
		}
		config.profiles.put(activeProfile.name(), moduleStates);

		try {
			Files.createDirectories(CONFIG_DIR);
			try (Writer writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
				GSON.toJson(config, writer);
			}
			ObsidianClientMod.LOGGER.info("Saved Obsidian Client config ({} profile) to {}", activeProfile, CONFIG_FILE);
		} catch (IOException e) {
			ObsidianClientMod.LOGGER.error("Failed to save Obsidian Client config", e);
		}
	}

	private static ObsidianConfig readExistingConfig() {
		if (!Files.exists(CONFIG_FILE)) {
			return new ObsidianConfig();
		}
		try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
			ObsidianConfig config = GSON.fromJson(reader, ObsidianConfig.class);
			if (config == null) {
				return new ObsidianConfig();
			}
			if (config.profiles == null) {
				config.profiles = new LinkedHashMap<>();
			}
			return config;
		} catch (IOException e) {
			ObsidianClientMod.LOGGER.warn("Could not read existing config before saving, starting fresh: {}", e.getMessage());
			return new ObsidianConfig();
		}
	}
}
