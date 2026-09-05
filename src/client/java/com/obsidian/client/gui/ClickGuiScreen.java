package com.obsidian.client.gui;

import com.obsidian.client.client.ObsidianClientModClient;
import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import com.obsidian.client.module.Profile;
import com.obsidian.client.module.setting.BooleanSetting;
import com.obsidian.client.module.setting.ColorSetting;
import com.obsidian.client.module.setting.EnumSetting;
import com.obsidian.client.module.setting.IntSetting;
import com.obsidian.client.module.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The Obsidian Client settings menu ("ClickGUI"). Deliberately built only from
 * stock {@link Button}/{@link EditBox} instances so all input
 * handling (clicks, drags, focus, typing) is delegated to vanilla widget code
 * rather than re-implemented here.
 */
public class ClickGuiScreen extends Screen {

	private static final int PANEL_WIDTH = 360;
	private static final int PANEL_HEIGHT = 280;
	private static final int HEADER_HEIGHT = 26;
	private static final int TAB_HEIGHT = 20;
	private static final int SEARCH_HEIGHT = 16;
	private static final int ROW_HEIGHT = 20;
	private static final int ROW_SPACING = 2;
	private static final int FOOTER_HEIGHT = 20;
	private static final int FADE_DURATION_MS = 150;

	/** Remembered across screen re-opens so the GUI reopens where you left it. */
	private static ModuleCategory selectedCategory = ModuleCategory.COMBAT;
	private static String searchQuery = "";
	/** Non-null while showing a module's settings pane instead of the module list. */
	private static Module settingsModule;

	private final long openTimeMs = System.currentTimeMillis();
	private final List<AbstractWidget> moduleRows = new ArrayList<>();

	private int panelX;
	private int panelY;
	private int tabWidth;
	private EditBox searchField;

	public ClickGuiScreen() {
		super(Component.literal("Obsidian Client"));
	}

	@Override
	protected void init() {
		panelX = (this.width - PANEL_WIDTH) / 2;
		panelY = (this.height - PANEL_HEIGHT) / 2;

		ModuleCategory[] categories = ModuleCategory.values();
		tabWidth = PANEL_WIDTH / categories.length;

		for (int i = 0; i < categories.length; i++) {
			ModuleCategory category = categories[i];
			int tabX = panelX + i * tabWidth;
			this.addRenderableWidget(Button.builder(Component.literal(category.getDisplayName()), btn -> {
				selectedCategory = category;
				settingsModule = null;
				rebuildModuleRows();
			}).bounds(tabX, panelY + HEADER_HEIGHT, tabWidth, TAB_HEIGHT).build());
		}

		int searchY = panelY + HEADER_HEIGHT + TAB_HEIGHT + 6;
		this.searchField = new EditBox(this.font, panelX + 8, searchY, PANEL_WIDTH - 16, SEARCH_HEIGHT, Component.literal("Search"));
		this.searchField.setMaxLength(32);
		this.searchField.setValue(searchQuery);
		this.searchField.setResponder(text -> {
			searchQuery = text;
			settingsModule = null;
			rebuildModuleRows();
		});
		this.addRenderableWidget(this.searchField);

		int themeButtonWidth = 60;
		this.addRenderableWidget(Button.builder(Component.literal("Theme"), btn -> Theme.toggleMode())
				.bounds(panelX + PANEL_WIDTH - themeButtonWidth * 2 - 12, panelY + 3, themeButtonWidth, HEADER_HEIGHT - 6)
				.build());
		this.addRenderableWidget(Button.builder(Component.literal("Accent"), btn -> Theme.cycleAccent())
				.bounds(panelX + PANEL_WIDTH - themeButtonWidth - 6, panelY + 3, themeButtonWidth, HEADER_HEIGHT - 6)
				.build());

		this.addRenderableWidget(Button.builder(profileLabel(), btn -> {
					cycleProfile();
					btn.setMessage(profileLabel());
				})
				.bounds(panelX, panelY + PANEL_HEIGHT - FOOTER_HEIGHT + 2, 100, FOOTER_HEIGHT - 4)
				.build());

		rebuildModuleRows();
	}

	private Component profileLabel() {
		return Component.literal("Profile: " + ObsidianClientModClient.getModuleManager().getActiveProfile().name());
	}

	private void cycleProfile() {
		Profile[] profiles = Profile.values();
		Profile current = ObsidianClientModClient.getModuleManager().getActiveProfile();
		Profile next = profiles[(current.ordinal() + 1) % profiles.length];
		ObsidianClientModClient.getModuleManager().setActiveProfile(next);
		rebuildModuleRows();
	}

	private void rebuildModuleRows() {
		for (AbstractWidget row : moduleRows) {
			this.removeWidget(row);
		}
		moduleRows.clear();

		if (settingsModule != null) {
			rebuildSettingsRows(settingsModule);
			return;
		}

		int y = panelY + HEADER_HEIGHT + TAB_HEIGHT + SEARCH_HEIGHT + 14;
		String query = searchQuery.toLowerCase();

		for (Module module : ObsidianClientModClient.getModuleManager().getModulesByCategory(selectedCategory)) {
			if (!query.isBlank() && !module.getName().toLowerCase().contains(query)) {
				continue;
			}

			boolean hasSettings = !module.getSettings().isEmpty();
			int gearWidth = 22;
			int toggleWidth = hasSettings ? PANEL_WIDTH - 16 - gearWidth - 4 : PANEL_WIDTH - 16;

			Button row = Button.builder(rowLabel(module), btn -> {
				ObsidianClientModClient.getModuleManager().toggle(module);
				btn.setMessage(rowLabel(module));
			}).bounds(panelX + 8, y, toggleWidth, ROW_HEIGHT - ROW_SPACING).build();
			this.addRenderableWidget(row);
			moduleRows.add(row);

			if (hasSettings) {
				Button gearButton = Button.builder(Component.literal("\u2699"), btn -> {
					settingsModule = module;
					rebuildModuleRows();
				}).bounds(panelX + 8 + toggleWidth + 4, y, gearWidth, ROW_HEIGHT - ROW_SPACING).build();
				this.addRenderableWidget(gearButton);
				moduleRows.add(gearButton);
			}

			y += ROW_HEIGHT;
		}
	}

	/** Builds the settings pane for a single module: a back row, then one click-to-cycle row per setting. */
	private void rebuildSettingsRows(Module module) {
		int y = panelY + HEADER_HEIGHT + TAB_HEIGHT + SEARCH_HEIGHT + 14;

		Button back = Button.builder(Component.literal("< " + module.getName()), btn -> {
			settingsModule = null;
			rebuildModuleRows();
		}).bounds(panelX + 8, y, PANEL_WIDTH - 16, ROW_HEIGHT - ROW_SPACING).build();
		this.addRenderableWidget(back);
		moduleRows.add(back);
		y += ROW_HEIGHT;

		for (Setting<?> setting : module.getSettings()) {
			Button row = Button.builder(settingLabel(setting), btn -> {
				cycleSetting(setting);
				btn.setMessage(settingLabel(setting));
			}).bounds(panelX + 8, y, PANEL_WIDTH - 16, ROW_HEIGHT - ROW_SPACING).build();
			this.addRenderableWidget(row);
			moduleRows.add(row);
			y += ROW_HEIGHT;
		}
	}

	private Component rowLabel(Module module) {
		return Component.literal(module.getName() + "   [" + (module.isEnabled() ? "ON" : "OFF") + "]"
				+ "  (" + module.getTier().getBadgeLabel() + ")");
	}

	private static Component settingLabel(Setting<?> setting) {
		return Component.literal(setting.getName() + ": " + settingValueText(setting));
	}

	private static String settingValueText(Setting<?> setting) {
		if (setting instanceof BooleanSetting bool) {
			return bool.get() ? "ON" : "OFF";
		}
		if (setting instanceof ColorSetting color) {
			return String.format("#%06X", color.getArgb() & 0xFFFFFF);
		}
		if (setting instanceof IntSetting intSetting) {
			return String.valueOf(intSetting.get());
		}
		if (setting instanceof EnumSetting<?> enumSetting) {
			return enumSetting.getValue().toString();
		}
		return String.valueOf(setting.getValue());
	}

	/** Click-to-cycle handling for every setting type; the ClickGUI has no drag/slider widgets. */
	private static void cycleSetting(Setting<?> setting) {
		if (setting instanceof BooleanSetting bool) {
			bool.toggle();
		} else if (setting instanceof ColorSetting color) {
			color.cyclePreset();
		} else if (setting instanceof IntSetting intSetting) {
			int step = Math.max(1, (intSetting.getMax() - intSetting.getMin()) / 10);
			int next = intSetting.get() + step;
			intSetting.setValue(next > intSetting.getMax() ? intSetting.getMin() : next);
		} else if (setting instanceof EnumSetting<?> enumSetting) {
			enumSetting.cycle();
		}
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		float alpha = fadeAlpha();
		this.fadeWidgets(alpha);

		context.fill(0, 0, this.width, this.height, withAlpha(Theme.scrim(), alpha));

		context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, withAlpha(Theme.panelBackground(), alpha));
		context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + HEADER_HEIGHT, withAlpha(Theme.headerBackground(), alpha));
		context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, withAlpha(Theme.accent(), alpha));

		// Highlight the selected category tab.
		int selectedIndex = java.util.Arrays.asList(ModuleCategory.values()).indexOf(selectedCategory);
		int tabX = panelX + selectedIndex * tabWidth;
		context.fill(tabX, panelY + HEADER_HEIGHT, tabX + tabWidth, panelY + HEADER_HEIGHT + TAB_HEIGHT, withAlpha(Theme.accent() & 0x66FFFFFF, alpha));

		context.drawString(this.font, this.title, panelX + 8, panelY + 9, withAlpha(Theme.textPrimary(), alpha));

		super.render(context, mouseX, mouseY, deltaTicks);
	}

	private float fadeAlpha() {
		long elapsed = System.currentTimeMillis() - openTimeMs;
		return Math.max(0f, Math.min(1f, elapsed / (float) FADE_DURATION_MS));
	}

	private static int withAlpha(int argb, float alphaFactor) {
		int originalAlpha = (argb >>> 24) & 0xFF;
		int scaledAlpha = Math.round(originalAlpha * alphaFactor);
		return (scaledAlpha << 24) | (argb & 0x00FFFFFF);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
