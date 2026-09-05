package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import org.lwjgl.glfw.GLFW;

/** Displays the player's block position and coarse compass facing. */
public class CoordinatesModule extends Module {

	private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

	public CoordinatesModule() {
		super("Coordinates", "Shows player position and facing direction.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		BlockPos pos = client.player.blockPosition();
		String facing = facingFor(client.player.getYRot());
		String text = String.format("XYZ: %d, %d, %d (%s)", pos.getX(), pos.getY(), pos.getZ(), facing);

		context.drawString(client.font, text, 4, 28, Theme.textPrimary());
	}

	private static String facingFor(float yaw) {
		float normalized = yaw % 360f;
		if (normalized < 0) {
			normalized += 360f;
		}
		int index = Math.round(normalized / 45f) & 7;
		return DIRECTIONS[index];
	}
}
