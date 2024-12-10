package se.sundsvall.businessrules.rule.impl.parkingpermit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.DurationEnum;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

class ParkingPermitRuleEngineUtilTest {

	@Test
	void createErrorMessageForMissingFact() {

		// Arrange
		final var factParameter = ParkingPermitFactKeyEnum.DISABILITY_DURATION;

		// Act
		final var result = ParkingPermitRuleEngineUtil.createErrorMessageForMissingFact(factParameter);

		// Assert
		assertThat(result).isEqualTo("Saknar giltigt värde för: 'disability.duration' (funktionsnedsättningens varaktighet)");
	}

	@Test
	void isValidDurationEnumValue() {
		Stream.of(DurationEnum.values()).forEach(item -> {
			assertThat(ParkingPermitRuleEngineUtil.isValidDurationEnumValue(item.getDuration().toLowerCase())).isTrue();
			assertThat(ParkingPermitRuleEngineUtil.isValidDurationEnumValue(item.getDuration())).isTrue();
		});

		assertThat(ParkingPermitRuleEngineUtil.isValidDurationEnumValue(null)).isFalse();
		assertThat(ParkingPermitRuleEngineUtil.isValidDurationEnumValue("randomValue")).isFalse();
	}
}
