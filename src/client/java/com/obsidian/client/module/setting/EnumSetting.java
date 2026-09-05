package com.obsidian.client.module.setting;

/** A setting whose value is one of a fixed enum's constants, rendered as a dropdown. */
public class EnumSetting<E extends Enum<E>> extends Setting<E> {

	private final Class<E> enumType;

	public EnumSetting(String name, String description, Class<E> enumType, E defaultValue) {
		super(name, description, defaultValue);
		this.enumType = enumType;
	}

	public E[] options() {
		return enumType.getEnumConstants();
	}

	/** Advances to the next enum constant, wrapping around. Handy for a single-click cycle button. */
	public void cycle() {
		E[] values = options();
		int nextIndex = (getValue().ordinal() + 1) % values.length;
		setValue(values[nextIndex]);
	}
}
