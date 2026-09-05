package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * Holds the vanilla sprint keybinding "pressed" while enabled, so the player
 * sprints automatically without holding the key. Equivalent in spirit to
 * vanilla's own "Sprint: Toggle" option, just exposed as a module.
 */
public class ToggleSprintModule extends Module {

	public ToggleSprintModule() {
		super("Toggle Sprint", "Automatically sprints while moving forward.", ModuleCategory.MOVEMENT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onTick() {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null && client.screen == null) {
			client.options.keySprint.setDown(true);
		}
	}
}
