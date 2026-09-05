package com.obsidian.client.gui;

import com.obsidian.client.client.ObsidianClientModClient;
import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * The Obsidian Client settings menu ("ClickGUI"). Deliberately built only from
 * stock {@link ButtonWidget}/{@link TextFieldWidget} instances so all input
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
	private static final int FADE_DURATION_MS = 150;

	/** Remembered across screen re-opens so the GUI reopens where you left it. */
	private static ModuleCategory selectedCategory = ModuleCategory.COMBAT;
	private static String searchQuery = "";

	private final long openTimeMs = System.currentTimeMillis();
	private final List<ClickableWidget> moduleRows = new ArrayList<>();

	private int panelX;
	private int panelY;
	private int tabWidth;
	private TextFieldWidget searchField;

	public ClickGuiScreen() {
		super(Text.literal("Obsidian Client"));
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
			this.addDrawableChild(ButtonWidget.builder(Text.literal(category.getDisplayName()), btn -> {
				selectedCategory = category;
				rebuildModuleRows();
			}).dimensions(tabX, panelY + HEADER_HEIGHT, tabWidth, TAB_HEIGHT).build());
		}

		int searchY = panelY + HEADER_HEIGHT + TAB_HEIGHT + 6;
		this.searchField = new TextFieldWidget(this.textRenderer, panelX + 8, searchY, PANEL_WIDTH - 16, SEARCH_HEIGHT, Text.literal("Search"));
		this.searchField.setMaxLength(32);
		this.searchField.setText(searchQuery);
		this.searchField.setChangedListener(text -> {
			searchQuery = text;
			rebuildModuleRows();
		});
		this.addDrawableChild(this.searchField);

		int themeButtonWidth = 60;
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Theme"), btn -> Theme.toggleMode())
				.dimensions(panelX + PANEL_WIDTH - themeButtonWidth * 2 - 12, panelY + 3, themeButtonWidth, HEADER_HEIGHT - 6)
				.build());
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Accent"), btn -> Theme.cycleAccent())
				.dimensions(panelX + PANEL_WIDTH - themeButtonWidth - 6, panelY + 3, themeButtonWidth, HEADER_HEIGHT - 6)
				.build());

		rebuildModuleRows();
	}

	private void rebuildModuleRows() {
		for (ClickableWidget row : moduleRows) {
			this.remove(row);
		}
		moduleRows.clear();

		int y = panelY + HEADER_HEIGHT + TAB_HEIGHT + SEARCH_HEIGHT + 14;
		String query = searchQuery.toLowerCase();

		for (Module module : ObsidianClientModClient.getModuleManager().getModulesByCategory(selectedCategory)) {
			if (!query.isBlank() && !module.getName().toLowerCase().contains(query)) {
				continue;
			}

			ButtonWidget row = ButtonWidget.builder(rowLabel(module), btn -> {
				ObsidianClientModClient.getModuleManager().toggle(module);
				btn.setMessage(rowLabel(module));
			}).dimensions(panelX + 8, y, PANEL_WIDTH - 16, ROW_HEIGHT - ROW_SPACING).build();

			this.addDrawableChild(row);
			moduleRows.add(row);
			y += ROW_HEIGHT;
		}
	}

	private Text rowLabel(Module module) {
		return Text.literal(module.getName() + "   [" + (module.isEnabled() ? "ON" : "OFF") + "]");
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		float alpha = fadeAlpha();
		this.setWidgetAlpha(alpha);

		context.fill(0, 0, this.width, this.height, withAlpha(Theme.scrim(), alpha));

		context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, withAlpha(Theme.panelBackground(), alpha));
		context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + HEADER_HEIGHT, withAlpha(Theme.headerBackground(), alpha));
		context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, withAlpha(Theme.accent(), alpha));

		// Highlight the selected category tab.
		int selectedIndex = java.util.Arrays.asList(ModuleCategory.values()).indexOf(selectedCategory);
		int tabX = panelX + selectedIndex * tabWidth;
		context.fill(tabX, panelY + HEADER_HEIGHT, tabX + tabWidth, panelY + HEADER_HEIGHT + TAB_HEIGHT, withAlpha(Theme.accent() & 0x66FFFFFF, alpha));

		context.drawTextWithShadow(this.textRenderer, this.title, panelX + 8, panelY + 9, withAlpha(Theme.textPrimary(), alpha));

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
	public boolean shouldPause() {
		return false;
	}
}
