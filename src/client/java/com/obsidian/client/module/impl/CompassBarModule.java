package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Draws a horizontal heading strip at the top of the screen showing the
 * cardinal/intercardinal directions around the player's current yaw, as a
 * graphical alternative to the text facing shown by Coordinates.
 */
public class CompassBarModule extends Module {

	// Same order/convention as CoordinatesModule: index i corresponds to yaw i*45 degrees.
	private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
	private static final int PIXELS_PER_DEGREE = 2;
	private static final int BAR_WIDTH = 180;
	private static final int BAR_HEIGHT = 12;

	public CompassBarModule() {
		super("Compass Bar", "Graphical heading strip showing nearby cardinal directions.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		int centerX = client.getWindow().getGuiScaledWidth() / 2;
		int y = 4;

		context.fill(centerX - BAR_WIDTH / 2, y, centerX + BAR_WIDTH / 2, y + BAR_HEIGHT, 0x60000000);

		float yaw = client.player.getYRot() % 360f;
		if (yaw < 0) {
			yaw += 360f;
		}

		for (int i = 0; i < DIRECTIONS.length; i++) {
			float directionAngle = i * 45f;
			float delta = wrap180(directionAngle - yaw);
			int screenX = centerX + Math.round(delta * PIXELS_PER_DEGREE);
			if (screenX < centerX - BAR_WIDTH / 2 || screenX > centerX + BAR_WIDTH / 2) {
				continue;
			}
			String label = DIRECTIONS[i];
			int labelX = screenX - client.font.width(label) / 2;
			context.drawString(client.font, label, labelX, y + 2, Theme.textPrimary());
		}

		// Fixed center tick marking the player's exact facing direction.
		context.fill(centerX - 1, y, centerX + 1, y + BAR_HEIGHT, Theme.accent());
	}

	/** Wraps a degree value to the range [-180, 180]. */
	private static float wrap180(float degrees) {
		return ((degrees + 180f) % 360f + 360f) % 360f - 180f;
	}
}
