package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side counter for consecutive attacks landed on entities, reset after
 * a short idle window (Lunar/Feather-style "Combo" HUD). Purely cosmetic:
 * it only observes the {@code AttackEntityCallback} hook wired in
 * {@code ObsidianClientModClient} and never alters hit registration, attack
 * cooldown, or damage.
 */
public class ComboCounterModule extends Module {

	/** Combo resets after this many ticks (3s at 20 TPS) without a landed hit. */
	private static final int IDLE_RESET_TICKS = 60;

	private int combo;
	private int idleTicks;

	public ComboCounterModule() {
		super("Combo Counter", "Displays your consecutive-hit streak.", ModuleCategory.COMBAT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	/** Called from the global AttackEntityCallback hook when the player lands an attack. */
	public void onHitLanded() {
		combo++;
		idleTicks = 0;
	}

	@Override
	public void onEnable() {
		combo = 0;
		idleTicks = 0;
	}

	@Override
	public void onTick() {
		if (combo > 0 && ++idleTicks > IDLE_RESET_TICKS) {
			combo = 0;
			idleTicks = 0;
		}
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null || combo <= 0) {
			return;
		}

		String text = "Combo: " + combo;
		int x = client.getWindow().getGuiScaledWidth() / 2 - client.font.width(text) / 2;
		int y = client.getWindow().getGuiScaledHeight() / 2 + 32;
		context.drawString(client.font, text, x, y, Theme.accent());
	}
}
