package com.obsidian.client.module.setting;

/**
 * A packed ARGB color value (e.g. {@code 0xFF7C5CFF}). The ClickGUI doesn't
 * implement a full RGB picker widget (it sticks to stock vanilla widgets
 * only — see {@code ClickGuiScreen}'s class doc), so this exposes a small
 * curated palette to click-cycle through instead, the same pattern used by
 * {@code Theme#cycleAccent()}.
 */
public class ColorSetting extends Setting<Integer> {

	private static final int[] PALETTE = {
			0xFFFFFFFF, // white (vanilla default)
			0xFF7C5CFF, // violet
			0xFF00C2FF, // cyan
			0xFF00E0A4, // teal/green
			0xFFFF5C7A, // rose
			0xFFFFB020, // amber
			0xFF2ECC71, // green
			0xFFE74C3C, // red
	};

	public ColorSetting(String name, String description, int defaultArgb) {
		super(name, description, defaultArgb);
	}

	public int getArgb() {
		return getValue();
	}

	/** Advances to the next preset in the curated palette, wrapping around. */
	public void cyclePreset() {
		int currentIndex = indexOfClosest(getArgb());
		setValue(PALETTE[(currentIndex + 1) % PALETTE.length]);
	}

	private static int indexOfClosest(int argb) {
		for (int i = 0; i < PALETTE.length; i++) {
			if (PALETTE[i] == argb) {
				return i;
			}
		}
		return -1;
	}
}
