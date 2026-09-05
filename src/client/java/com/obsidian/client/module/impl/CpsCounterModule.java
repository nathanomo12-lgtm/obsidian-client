package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Clicks-per-second counter for the left and right mouse buttons, sampled
 * every render frame (rather than every tick) for finer click resolution.
 */
public class CpsCounterModule extends Module {

	private static final long WINDOW_MS = 1000;

	private final Deque<Long> leftClicks = new ArrayDeque<>();
	private final Deque<Long> rightClicks = new ArrayDeque<>();
	private boolean lastLeftDown;
	private boolean lastRightDown;

	public CpsCounterModule() {
		super("CPS Counter", "Displays left/right click rate.", ModuleCategory.COMBAT, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onDisable() {
		leftClicks.clear();
		rightClicks.clear();
		lastLeftDown = false;
		lastRightDown = false;
	}

	@Override
	public void onRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) {
			return;
		}

		long handle = client.getWindow().getHandle();
		long now = System.currentTimeMillis();

		boolean leftDown = client.currentScreen == null
				&& GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		boolean rightDown = client.currentScreen == null
				&& GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

		if (leftDown && !lastLeftDown) {
			leftClicks.addLast(now);
		}
		if (rightDown && !lastRightDown) {
			rightClicks.addLast(now);
		}
		lastLeftDown = leftDown;
		lastRightDown = rightDown;

		prune(leftClicks, now);
		prune(rightClicks, now);

		if (client.currentScreen != null) {
			return;
		}

		String text = "CPS: " + leftClicks.size() + " / " + rightClicks.size();
		int x = client.getWindow().getScaledWidth() / 2 - client.textRenderer.getWidth(text) / 2;
		int y = client.getWindow().getScaledHeight() / 2 + 20;
		context.drawTextWithShadow(client.textRenderer, text, x, y, Theme.textPrimary());
	}

	private static void prune(Deque<Long> timestamps, long now) {
		while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MS) {
			timestamps.pollFirst();
		}
	}
}
