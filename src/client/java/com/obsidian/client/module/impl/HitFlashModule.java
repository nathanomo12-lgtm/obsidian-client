package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Briefly flashes a thin colored border around the screen edges when the
 * player's own attack lands, for extra visual feedback. Fed by the same
 * {@code AttackEntityCallback} hook wired in {@code ObsidianClientModClient}
 * (which only ever returns {@code PASS}) — purely cosmetic, never influences
 * hit registration, cooldown, or damage.
 */
public class HitFlashModule extends Module {

	private static final int FLASH_TICKS = 6;
	private static final int BORDER_THICKNESS = 3;

	private int flashTicksRemaining;

	public HitFlashModule() {
		super("Hit Flash", "Flashes a border around the screen when your attack lands.", ModuleCategory.COMBAT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	/** Called from the global AttackEntityCallback hook when the player lands an attack. */
	public void onHitLanded() {
		flashTicksRemaining = FLASH_TICKS;
	}

	@Override
	public void onDisable() {
		flashTicksRemaining = 0;
	}

	@Override
	public void onTick() {
		if (flashTicksRemaining > 0) {
			flashTicksRemaining--;
		}
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		if (flashTicksRemaining <= 0) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		int alpha = Math.round(0x80 * (flashTicksRemaining / (float) FLASH_TICKS));
		int color = (alpha << 24) | (Theme.accent() & 0x00FFFFFF);
		int width = client.getWindow().getGuiScaledWidth();
		int height = client.getWindow().getGuiScaledHeight();

		context.fill(0, 0, width, BORDER_THICKNESS, color);
		context.fill(0, height - BORDER_THICKNESS, width, height, color);
		context.fill(0, 0, BORDER_THICKNESS, height, color);
		context.fill(width - BORDER_THICKNESS, 0, width, height, color);
	}
}
