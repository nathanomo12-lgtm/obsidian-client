package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import com.obsidian.client.module.setting.IntSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * Optical zoom: lowers the vanilla FOV option while enabled and restores the
 * user's own FOV setting on disable. Purely a camera/optics change — it does
 * not alter aim, hitboxes, or any gameplay value, but some servers restrict
 * any FOV manipulation, so this is {@link ComplianceTier#SERVER_DEPENDENT}
 * and off by default under the Competitive profile.
 */
public class ZoomModule extends Module {

	private final IntSetting zoomFov = addSetting(new IntSetting("Zoom FOV", "Field of view while zoomed.", 15, 5, 50));

	private int previousFov = -1;

	public ZoomModule() {
		super("Zoom", "Reduces FOV for an optical zoom while enabled.", ModuleCategory.RENDER, ComplianceTier.SERVER_DEPENDENT, GLFW.GLFW_KEY_C);
	}

	@Override
	public void onEnable() {
		Minecraft client = Minecraft.getInstance();
		previousFov = client.options.fov().get();
		client.options.fov().set(zoomFov.get());
	}

	@Override
	public void onDisable() {
		if (previousFov != -1) {
			Minecraft.getInstance().options.fov().set(previousFov);
			previousFov = -1;
		}
	}
}
