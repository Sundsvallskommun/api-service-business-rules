package se.sundsvall.businessrules.rule.impl.parkingpermit.enums;

import java.util.Arrays;

public enum DurationEnum {
	LESS_THAN_6_MONTHS("P6M"),
	LESS_THAN_1_YEAR("P1Y"),
	LESS_THAN_2_YEARS("P2Y"),
	LESS_THAN_3_YEARS("P3Y"),
	LESS_THAN_4_YEARS("P4Y"),
	EQUAL_TO_OR_MORE_THAN_4_YEARS("P5Y"),
	PERMANENT("P0Y");

	private final String duration;

	DurationEnum(String duration) {
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}

	public boolean isEqualTo(String duration) {
		return this.duration.equalsIgnoreCase(duration);
	}

	public static DurationEnum fromDuration(String duration) {
		return Arrays.stream(DurationEnum.values())
			.filter(item -> item.getDuration().equalsIgnoreCase(duration))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Unsupported duration value '%s'!".formatted(duration)));
	}
}
