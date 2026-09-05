package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import org.lwjgl.glfw.GLFW;

/**
 * Compact list of the player's active potion effects and remaining duration,
 * as an alternative to vanilla's inventory-screen-only effect icons.
 */
public class PotionEffectsHudModule extends Module {

	public PotionEffectsHudModule() {
		super("Potion Effects", "Lists active potion effects and remaining duration.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		int y = 40;
		for (MobEffectInstance effect : client.player.getActiveEffects()) {
			String name = effect.getEffect().value().getDisplayName().getString();
			int amplifier = effect.getAmplifier() + 1;
			String duration = formatDuration(effect.getDuration());
			String text = name + (amplifier > 1 ? " " + amplifier : "") + " (" + duration + ")";
			context.drawString(client.font, text, 4, y, Theme.textPrimary());
			y += 10;
		}
	}

	/** Formats a duration in ticks as {@code m:ss}, matching vanilla's own effect tooltip format. */
	private static String formatDuration(int ticks) {
		int totalSeconds = ticks / 20;
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;
		return minutes + ":" + (seconds < 10 ? "0" + seconds : String.valueOf(seconds));
	}
}
