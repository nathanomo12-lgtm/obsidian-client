package com.obsidian.client.gui.theme;

/**
 * Central color palette for the ClickGUI and HUD. Colors are packed ARGB ints,
 * matching what {@code DrawContext#fill}/{@code drawText} expect.
 *
 * <p>Kept intentionally simple: a handful of static, mutable fields rather than
 * a full theming engine. {@link #cycleAccent()} and {@link #toggleMode()} are
 * wired to small buttons in the ClickGUI header.</p>
 */
public final class Theme {

	/** Curated accent color presets the user can cycle through. */
	private static final int[] ACCENTS = {
			0xFF7C5CFF, // violet (default)
			0xFF00C2FF, // cyan
			0xFF00E0A4, // teal/green
			0xFFFF5C7A, // rose
			0xFFFFB020, // amber
	};

	private static int accentIndex = 0;
	private static boolean darkMode = true;

	private Theme() {
	}

	public static boolean isDarkMode() {
		return darkMode;
	}

	public static void toggleMode() {
		darkMode = !darkMode;
	}

	public static int accent() {
		return ACCENTS[accentIndex];
	}

	public static void cycleAccent() {
		accentIndex = (accentIndex + 1) % ACCENTS.length;
	}

	/** Translucent full-screen dimming behind the ClickGUI panel. */
	public static int scrim() {
		return darkMode ? 0x99000000 : 0x66000000;
	}

	public static int panelBackground() {
		return darkMode ? 0xF0161616 : 0xF0EDEDED;
	}

	public static int panelBorder() {
		return accent();
	}

	public static int headerBackground() {
		return darkMode ? 0xFF1E1E1E : 0xFFDCDCDC;
	}

	public static int rowBackground(boolean hovered) {
		if (hovered) {
			return darkMode ? 0xFF2A2A2A : 0xFFD5D5D5;
		}
		return darkMode ? 0xFF202020 : 0xFFE2E2E2;
	}

	public static int textPrimary() {
		return darkMode ? 0xFFF2F2F2 : 0xFF101010;
	}

	public static int textSecondary() {
		return darkMode ? 0xFFA0A0A0 : 0xFF4A4A4A;
	}

	public static int moduleEnabled() {
		return 0xFF2ECC71;
	}

	public static int moduleDisabled() {
		return darkMode ? 0xFF4A4A4A : 0xFFB5B5B5;
	}
}
