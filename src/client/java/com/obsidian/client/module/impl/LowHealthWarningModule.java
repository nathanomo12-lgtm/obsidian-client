package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import com.obsidian.client.module.setting.IntSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Tints the screen edges red, pulsing, whenever health drops below a
 * configurable threshold — a purely cosmetic low-health warning, distinct
 * from vanilla's own damage flash, that stays visible for as long as health
 * remains low rather than a one-shot flash.
 */
public class LowHealthWarningModule extends Module {

	private static final int BORDER_THICKNESS = 6;

	private final IntSetting threshold = addSetting(new IntSetting("Threshold", "Health at or below which the warning shows.", 6, 1, 19));

	private int pulseTicks;

	public LowHealthWarningModule() {
		super("Low Health Warning", "Pulses a red screen border when health is low.", ModuleCategory.RENDER, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onDisable() {
		pulseTicks = 0;
	}

	@Override
	public void onTick() {
		pulseTicks++;
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		if (client.player.getHealth() > threshold.get()) {
			return;
		}

		float pulse = (float) (Math.sin(pulseTicks * 0.2) * 0.5 + 0.5);
		int alpha = Math.round(0x30 + pulse * 0x40);
		int color = (alpha << 24) | 0x00E74C3C;
		int width = client.getWindow().getGuiScaledWidth();
		int height = client.getWindow().getGuiScaledHeight();

		context.fill(0, 0, width, BORDER_THICKNESS, color);
		context.fill(0, height - BORDER_THICKNESS, width, height, color);
		context.fill(0, 0, BORDER_THICKNESS, height, color);
		context.fill(width - BORDER_THICKNESS, 0, width, height, color);
	}
}
