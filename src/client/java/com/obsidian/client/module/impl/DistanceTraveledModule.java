package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

/** Tracks total distance the player has moved since this module was enabled. */
public class DistanceTraveledModule extends Module {

	private double blocksTraveled;

	public DistanceTraveledModule() {
		super("Distance Traveled", "Tracks total blocks moved since enabled.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onEnable() {
		blocksTraveled = 0;
	}

	@Override
	public void onTick() {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			return;
		}
		Vec3 previous = client.player.oldPosition();
		Vec3 current = client.player.position();
		blocksTraveled += previous.distanceTo(current);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		String text = String.format("Distance: %.0f blocks", blocksTraveled);
		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 88, Theme.textPrimary());
	}
}
