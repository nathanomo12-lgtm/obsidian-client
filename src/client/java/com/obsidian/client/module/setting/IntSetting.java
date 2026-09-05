package com.obsidian.client.module.setting;

/** An integer value clamped to [min, max], rendered as a slider. */
public class IntSetting extends Setting<Integer> {

	private final int min;
	private final int max;

	public IntSetting(String name, String description, int defaultValue, int min, int max) {
		super(name, description, clamp(defaultValue, min, max));
		this.min = min;
		this.max = max;
	}

	public int get() {
		return getValue();
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	@Override
	protected Integer coerce(Integer value) {
		return clamp(value, min, max);
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
