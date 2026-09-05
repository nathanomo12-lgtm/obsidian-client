package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * Holds the vanilla sneak keybinding "pressed" while enabled, equivalent to
 * vanilla's own "Sneak: Toggle" option, exposed as a module.
 */
public class ToggleSneakModule extends Module {

	public ToggleSneakModule() {
		super("Toggle Sneak", "Sneaks continuously without holding the key.", ModuleCategory.MOVEMENT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onTick() {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null && client.screen == null) {
			client.options.keyShift.setDown(true);
		}
	}
}
