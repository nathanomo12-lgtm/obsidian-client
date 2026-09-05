package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

/**
 * Shows the name, position, and face of the block the player is currently
 * looking at, reusing the same {@code Minecraft#hitResult} vanilla already
 * computes for block interaction/placement — no new raycast is performed.
 */
public class TargetBlockInfoModule extends Module {

	public TargetBlockInfoModule() {
		super("Target Block Info", "Shows the block you're looking at and its position.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null || client.level == null) {
			return;
		}

		HitResult hit = client.hitResult;
		if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}

		BlockPos pos = blockHit.getBlockPos();
		BlockState state = client.level.getBlockState(pos);
		String name = state.getBlock().getName().getString();
		String face = blockHit.getDirection().getName();
		String text = String.format("%s (%d, %d, %d) [%s]", name, pos.getX(), pos.getY(), pos.getZ(), face);

		int x = client.getWindow().getGuiScaledWidth() / 2 - client.font.width(text) / 2;
		int y = client.getWindow().getGuiScaledHeight() / 2 + 44;
		context.drawString(client.font, text, x, y, Theme.textSecondary());
	}
}
