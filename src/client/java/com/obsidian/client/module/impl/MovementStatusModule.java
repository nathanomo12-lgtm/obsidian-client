package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

/** Displays the player's current movement state (sprinting/sneaking/swimming/gliding). */
public class MovementStatusModule extends Module {

	public MovementStatusModule() {
		super("Movement Status", "Shows your current sprinting/sneaking/swimming/gliding state.", ModuleCategory.MOVEMENT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null || client.screen != null) {
			return;
		}

		String status;
		if (player.isFallFlying()) {
			status = "Gliding";
		} else if (player.isSwimming()) {
			status = "Swimming";
		} else if (player.isSprinting()) {
			status = "Sprinting";
		} else if (player.isCrouching()) {
			status = "Sneaking";
		} else {
			status = "Walking";
		}

		int y = client.getWindow().getGuiScaledHeight() - 40;
		context.drawString(client.font, status, 4, y, Theme.textPrimary());
	}
}
