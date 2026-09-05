package com.obsidian.client.client;

import com.obsidian.client.ObsidianClientMod;
import com.obsidian.client.config.ConfigManager;
import com.obsidian.client.gui.ClickGuiScreen;
import com.obsidian.client.module.ModuleManager;
import com.obsidian.client.module.impl.ArmorDurabilityModule;
import com.obsidian.client.module.impl.CoordinatesModule;
import com.obsidian.client.module.impl.CpsCounterModule;
import com.obsidian.client.module.impl.ExampleModule;
import com.obsidian.client.module.impl.FpsDisplayModule;
import com.obsidian.client.module.impl.KeystrokesModule;
import com.obsidian.client.module.impl.PingDisplayModule;
import com.obsidian.client.module.impl.ToggleSneakModule;
import com.obsidian.client.module.impl.ToggleSprintModule;
import com.obsidian.client.module.Module;
import com.obsidian.client.util.VersionCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only entrypoint. This is where the module registry is built and
 * wired into Fabric's tick/render/lifecycle events.
 */
public class ObsidianClientModClient implements ClientModInitializer {

	private static final ModuleManager MODULE_MANAGER = new ModuleManager();

	private static final KeyBinding OPEN_CLICK_GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.obsidian-client.click_gui",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			Module.KEY_CATEGORY
	));

	public static ModuleManager getModuleManager() {
		return MODULE_MANAGER;
	}

	@Override
	public void onInitializeClient() {
		VersionCompat.check();

		registerModules();
		ConfigManager.load(MODULE_MANAGER);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MODULE_MANAGER.onClientTick();

			while (OPEN_CLICK_GUI_KEY.wasPressed()) {
				if (client.currentScreen == null && client.player != null) {
					client.setScreen(new ClickGuiScreen());
				}
			}
		});

		// HudRenderCallback is deprecated as of Fabric API 0.116; HudElementRegistry is the
		// current, non-deprecated way to draw custom HUD layers. Rendering after the crosshair
		// keeps our modules layered on top of the vanilla HUD.
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.CROSSHAIR,
				Identifier.of("obsidian-client", "hud"),
				(context, tickCounter) -> MODULE_MANAGER.onHudRender(context, tickCounter.getTickProgress(true)));

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(MODULE_MANAGER));

		ObsidianClientMod.LOGGER.info("Obsidian Client initialized with {} modules.", MODULE_MANAGER.getModules().size());
	}

	/** Registers every built-in module. Add new modules here as they're implemented. */
	private void registerModules() {
		MODULE_MANAGER.register(new ExampleModule());
		MODULE_MANAGER.register(new KeystrokesModule());
		MODULE_MANAGER.register(new CpsCounterModule());
		MODULE_MANAGER.register(new FpsDisplayModule());
		MODULE_MANAGER.register(new PingDisplayModule());
		MODULE_MANAGER.register(new ToggleSprintModule());
		MODULE_MANAGER.register(new ToggleSneakModule());
		MODULE_MANAGER.register(new CoordinatesModule());
		MODULE_MANAGER.register(new ArmorDurabilityModule());
	}
}
