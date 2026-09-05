package com.obsidian.client.module.setting;

public class BooleanSetting extends Setting<Boolean> {

	public BooleanSetting(String name, String description, boolean defaultValue) {
		super(name, description, defaultValue);
	}

	public boolean get() {
		return getValue();
	}

	public void toggle() {
		setValue(!getValue());
	}
}
