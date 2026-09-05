package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Lunar/Feather-style keystroke display: highlights WASD and the two primary
 * mouse buttons while they're held.
 */
public class KeystrokesModule extends Module {

	private static final int KEY_SIZE = 18;
	private static final int GAP = 2;

	public KeystrokesModule() {
		super("Keystrokes", "Shows WASD and mouse button presses.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		long handle = client.getWindow().handle();

		int baseX = client.getWindow().getGuiScaledWidth() - (KEY_SIZE * 3 + GAP * 2) - 10;
		int baseY = client.getWindow().getGuiScaledHeight() - (KEY_SIZE * 3 + GAP * 2) - 60;

		KeyMapping forward = client.options.keyUp;
		KeyMapping back = client.options.keyDown;
		KeyMapping left = client.options.keyLeft;
		KeyMapping right = client.options.keyRight;

		drawKey(context, baseX + KEY_SIZE + GAP, baseY, "W", forward.isDown());
		drawKey(context, baseX, baseY + KEY_SIZE + GAP, "A", left.isDown());
		drawKey(context, baseX + KEY_SIZE + GAP, baseY + KEY_SIZE + GAP, "S", back.isDown());
		drawKey(context, baseX + (KEY_SIZE + GAP) * 2, baseY + KEY_SIZE + GAP, "D", right.isDown());

		boolean leftMouse = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		boolean rightMouse = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

		int mouseRowY = baseY + (KEY_SIZE + GAP) * 2 + 4;
		int mouseWidth = (KEY_SIZE * 3 + GAP * 2 - 4) / 2;
		drawKeyRect(context, baseX, mouseRowY, mouseWidth, KEY_SIZE, "LMB", leftMouse);
		drawKeyRect(context, baseX + mouseWidth + 4, mouseRowY, mouseWidth, KEY_SIZE, "RMB", rightMouse);
	}

	private void drawKey(GuiGraphics context, int x, int y, String label, boolean pressed) {
		drawKeyRect(context, x, y, KEY_SIZE, KEY_SIZE, label, pressed);
	}

	private void drawKeyRect(GuiGraphics context, int x, int y, int width, int height, String label, boolean pressed) {
		Minecraft client = Minecraft.getInstance();
		int background = pressed ? Theme.accent() : Theme.rowBackground(false);
		context.fill(x, y, x + width, y + height, background);

		int textColor = pressed ? 0xFF000000 : Theme.textSecondary();
		int textWidth = client.font.width(label);
		context.drawString(client.font, label, x + (width - textWidth) / 2, y + (height - 8) / 2, textColor, false);
	}
}
