package com.obsidian.client.module.setting;

/**
 * A single configurable value belonging to a {@link com.obsidian.client.module.Module},
 * shown in the ClickGUI's settings pane (slider, color picker, dropdown, etc.
 * depending on subtype).
 *
 * @param <T> the value type
 */
public abstract class Setting<T> {

	private final String name;
	private final String description;
	private T value;

	protected Setting(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.value = defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public T getValue() {
		return value;
	}

	public final void setValue(T value) {
		this.value = coerce(value);
	}

	/** Hook for subclasses to clamp/validate an incoming value (e.g. slider range). */
	protected T coerce(T value) {
		return value;
	}
}
