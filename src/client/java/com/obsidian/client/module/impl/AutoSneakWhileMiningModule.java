package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * Automatically holds the sneak key while the player is actively breaking a
 * block, releasing it the moment mining stops — a QoL edge-safety aid (avoids
 * accidentally walking off a ledge while mining), not general automation.
 */
public class AutoSneakWhileMiningModule extends Module {

	private boolean wasMining;

	public AutoSneakWhileMiningModule() {
		super("Auto Sneak While Mining", "Holds sneak automatically while breaking a block.", ModuleCategory.MOVEMENT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onDisable() {
		if (wasMining) {
			Minecraft.getInstance().options.keyShift.setDown(false);
			wasMining = false;
		}
	}

	@Override
	public void onTick() {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null || client.gameMode == null) {
			return;
		}

		boolean mining = client.gameMode.isDestroying();
		if (mining) {
			client.options.keyShift.setDown(true);
		} else if (wasMining) {
			client.options.keyShift.setDown(false);
		}
		wasMining = mining;
	}
}
