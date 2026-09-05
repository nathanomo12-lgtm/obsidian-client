package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

/** Displays the player's current horizontal movement speed in blocks per second. */
public class SpeedDisplayModule extends Module {

	public SpeedDisplayModule() {
		super("Speed Display", "Shows horizontal movement speed in blocks/second.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		Vec3 velocity = client.player.getDeltaMovement();
		double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
		double blocksPerSecond = horizontal * 20.0;

		String text = String.format("Speed: %.1f b/s", blocksPerSecond);
		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 52, Theme.textPrimary());
	}
}
