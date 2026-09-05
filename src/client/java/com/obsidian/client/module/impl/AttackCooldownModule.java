package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Draws a small bar above the crosshair showing {@link net.minecraft.world.entity.player.Player#getAttackStrengthScale(float)},
 * the same publicly-exposed value vanilla uses for its own attack-indicator
 * option. Purely a rendering aid — it does not change the underlying attack
 * cooldown timing, damage, or hit registration in any way.
 */
public class AttackCooldownModule extends Module {

	private static final int WIDTH = 40;
	private static final int HEIGHT = 3;

	public AttackCooldownModule() {
		super("Attack Cooldown", "Shows a bar for your attack cooldown recovery.", ModuleCategory.COMBAT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		float scale = client.player.getAttackStrengthScale(tickDelta);
		if (scale >= 1.0f) {
			return;
		}

		int centerX = client.getWindow().getGuiScaledWidth() / 2;
		int centerY = client.getWindow().getGuiScaledHeight() / 2;
		int x = centerX - WIDTH / 2;
		int y = centerY - 18;

		context.fill(x, y, x + WIDTH, y + HEIGHT, 0x80000000);
		int filledWidth = Math.round(WIDTH * scale);
		int color = scale >= 1.0f ? 0xFF2ECC71 : 0xFFF1C40F;
		context.fill(x, y, x + filledWidth, y + HEIGHT, color);
	}
}
