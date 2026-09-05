package com.obsidian.client.module.impl;

import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/**
 * Holds the vanilla sprint keybinding "pressed" while enabled, so the player
 * sprints automatically without holding the key. Equivalent in spirit to
 * vanilla's own "Sprint: Toggle" option, just exposed as a module.
 */
public class ToggleSprintModule extends Module {

	public ToggleSprintModule() {
		super("Toggle Sprint", "Automatically sprints while moving forward.", ModuleCategory.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onTick() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null && client.currentScreen == null) {
			client.options.sprintKey.setPressed(true);
		}
	}
}
