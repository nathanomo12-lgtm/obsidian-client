package com.obsidian.client.module.impl;

import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/**
 * Holds the vanilla sneak keybinding "pressed" while enabled, equivalent to
 * vanilla's own "Sneak: Toggle" option, exposed as a module.
 */
public class ToggleSneakModule extends Module {

	public ToggleSneakModule() {
		super("Toggle Sneak", "Sneaks continuously without holding the key.", ModuleCategory.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onTick() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null && client.currentScreen == null) {
			client.options.sneakKey.setPressed(true);
		}
	}
}
