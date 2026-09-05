package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import com.obsidian.client.module.setting.ColorSetting;
import com.obsidian.client.module.setting.EnumSetting;
import com.obsidian.client.module.setting.IntSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Replaces the vanilla crosshair with a custom shape/color/size while enabled.
 * Purely cosmetic: it draws in the exact same screen-center position as the
 * vanilla crosshair and does not change reach, hit detection, or reveal any
 * information the vanilla crosshair doesn't already show.
 *
 * <p>Actual rendering is dispatched from the {@code HudElementRegistry#replaceElement}
 * wrapper installed in {@code ObsidianClientModClient}, which falls back to the
 * vanilla crosshair whenever this module is disabled.</p>
 */
public class CrosshairModule extends Module {

	/** Crosshair shapes offered in the ClickGUI's settings pane. */
	public enum Shape {
		CROSS, DOT, CIRCLE, T
	}

	private final EnumSetting<Shape> shape = addSetting(new EnumSetting<>("Shape", "Crosshair shape.", Shape.class, Shape.CROSS));
	private final ColorSetting color = addSetting(new ColorSetting("Color", "Crosshair color.", 0xFFFFFFFF));
	private final IntSetting size = addSetting(new IntSetting("Size", "Crosshair size in pixels.", 6, 4, 16));

	public CrosshairModule() {
		super("Crosshair", "Replaces the vanilla crosshair with a custom shape/color/size.", ModuleCategory.RENDER, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	/** Draws the custom crosshair centered on screen. Called only while this module is enabled. */
	public void render(GuiGraphics context) {
		Minecraft client = Minecraft.getInstance();
		int centerX = client.getWindow().getGuiScaledWidth() / 2;
		int centerY = client.getWindow().getGuiScaledHeight() / 2;
		int argb = color.getArgb();
		int half = Math.max(2, size.get() / 2);

		switch (shape.getValue()) {
			case DOT -> context.fill(centerX - half / 2, centerY - half / 2, centerX + half / 2, centerY + half / 2, argb);
			case CIRCLE -> renderCircleOutline(context, centerX, centerY, half, argb);
			case T -> {
				context.fill(centerX - half, centerY - 1, centerX + half, centerY + 1, argb);
				context.fill(centerX - 1, centerY - 1, centerX + 1, centerY + half, argb);
			}
			case CROSS -> {
				context.fill(centerX - half, centerY - 1, centerX + half, centerY + 1, argb);
				context.fill(centerX - 1, centerY - half, centerX + 1, centerY + half, argb);
			}
		}
	}

	/** Approximates a circle outline with 1px-wide horizontal spans; cheap and allocation-free. */
	private static void renderCircleOutline(GuiGraphics context, int centerX, int centerY, int radius, int argb) {
		for (int dy = -radius; dy <= radius; dy++) {
			int dx = (int) Math.round(Math.sqrt((double) radius * radius - (double) dy * dy));
			context.fill(centerX - dx, centerY + dy, centerX - dx + 1, centerY + dy + 1, argb);
			context.fill(centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, argb);
		}
	}
}
