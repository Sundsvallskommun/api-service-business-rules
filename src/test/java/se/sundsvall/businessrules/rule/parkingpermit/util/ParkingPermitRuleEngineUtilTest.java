package se.sundsvall.businessrules.rule.parkingpermit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum;

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
}
