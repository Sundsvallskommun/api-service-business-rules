package se.sundsvall.businessrules.rule.impl.parkingpermit.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class DurationEnumTest {

	@Test
	void validDurationValues() {
		assertThat(Stream.of(DurationEnum.values())
			.map(DurationEnum::getDuration)
			.toList()).containsExactlyInAnyOrder("P6M", "P1Y", "P2Y", "P3Y", "P4Y", "P5Y", "P0Y");
	}

	@Test
	void enumValues() {
		assertThat(DurationEnum.LESS_THAN_6_MONTHS.getDuration()).isEqualTo("P6M");
		assertThat(DurationEnum.LESS_THAN_1_YEAR.getDuration()).isEqualTo("P1Y");
		assertThat(DurationEnum.LESS_THAN_2_YEARS.getDuration()).isEqualTo("P2Y");
		assertThat(DurationEnum.LESS_THAN_3_YEARS.getDuration()).isEqualTo("P3Y");
		assertThat(DurationEnum.LESS_THAN_4_YEARS.getDuration()).isEqualTo("P4Y");
		assertThat(DurationEnum.EQUAL_TO_OR_MORE_THAN_4_YEARS.getDuration()).isEqualTo("P5Y");
		assertThat(DurationEnum.PERMANENT.getDuration()).isEqualTo("P0Y");
	}

	@Test
	void isEqualTo() {
		Stream.of(DurationEnum.values()).forEach(item -> assertThat(item.isEqualTo(item.getDuration())).isTrue());
	}

	@Test
	void fromDuration() {
		Stream.of(DurationEnum.values()).forEach(item -> assertThat(DurationEnum.fromDuration(item.getDuration())).isEqualTo(item));

		final var e = assertThrows(IllegalStateException.class, () -> DurationEnum.fromDuration("invalid-value"));
		assertThat(e.getMessage()).isEqualTo("Unsupported duration value 'invalid-value'!");
	}
}
