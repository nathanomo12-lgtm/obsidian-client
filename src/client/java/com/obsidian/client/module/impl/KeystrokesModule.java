package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

/**
 * Lunar/Feather-style keystroke display: highlights WASD and the two primary
 * mouse buttons while they're held.
 */
public class KeystrokesModule extends Module {

	private static final int KEY_SIZE = 18;
	private static final int GAP = 2;

	public KeystrokesModule() {
		super("Keystrokes", "Shows WASD and mouse button presses.", ModuleCategory.UTILITY, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.currentScreen != null) {
			return;
		}

		long handle = client.getWindow().getHandle();

		int baseX = client.getWindow().getScaledWidth() - (KEY_SIZE * 3 + GAP * 2) - 10;
		int baseY = client.getWindow().getScaledHeight() - (KEY_SIZE * 3 + GAP * 2) - 60;

		KeyBinding forward = client.options.forwardKey;
		KeyBinding back = client.options.backKey;
		KeyBinding left = client.options.leftKey;
		KeyBinding right = client.options.rightKey;

		drawKey(context, baseX + KEY_SIZE + GAP, baseY, "W", forward.isPressed());
		drawKey(context, baseX, baseY + KEY_SIZE + GAP, "A", left.isPressed());
		drawKey(context, baseX + KEY_SIZE + GAP, baseY + KEY_SIZE + GAP, "S", back.isPressed());
		drawKey(context, baseX + (KEY_SIZE + GAP) * 2, baseY + KEY_SIZE + GAP, "D", right.isPressed());

		boolean leftMouse = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		boolean rightMouse = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

		int mouseRowY = baseY + (KEY_SIZE + GAP) * 2 + 4;
		int mouseWidth = (KEY_SIZE * 3 + GAP * 2 - 4) / 2;
		drawKeyRect(context, baseX, mouseRowY, mouseWidth, KEY_SIZE, "LMB", leftMouse);
		drawKeyRect(context, baseX + mouseWidth + 4, mouseRowY, mouseWidth, KEY_SIZE, "RMB", rightMouse);
	}

	private void drawKey(DrawContext context, int x, int y, String label, boolean pressed) {
		drawKeyRect(context, x, y, KEY_SIZE, KEY_SIZE, label, pressed);
	}

	private void drawKeyRect(DrawContext context, int x, int y, int width, int height, String label, boolean pressed) {
		MinecraftClient client = MinecraftClient.getInstance();
		int background = pressed ? Theme.accent() : Theme.rowBackground(false);
		context.fill(x, y, x + width, y + height, background);

		int textColor = pressed ? 0xFF000000 : Theme.textSecondary();
		int textWidth = client.textRenderer.getWidth(label);
		context.drawText(client.textRenderer, label, x + (width - textWidth) / 2, y + (height - 8) / 2, textColor, false);
	}
}
