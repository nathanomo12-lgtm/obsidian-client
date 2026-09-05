package com.obsidian.client.module;

/**
 * Categories used to group modules in the ClickGUI tabs.
 */
public enum ModuleCategory {
	COMBAT("Combat"),
	MOVEMENT("Movement"),
	RENDER("Render"),
	UTILITY("Utility"),
	PLAYER("Player");

	private final String displayName;

	ModuleCategory(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
