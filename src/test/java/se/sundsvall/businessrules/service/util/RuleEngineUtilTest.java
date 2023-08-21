package se.sundsvall.businessrules.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.TestUtils.TestRule;
import se.sundsvall.businessrules.api.model.Result;

class RuleEngineUtilTest {

	@Test
	void nonApplicableResult() {

		// Arrange
		final var rule = new TestRule();

		// Act
		final var result = RuleEngineUtil.nonApplicableResult(rule);

		// Assert
		assertThat(result).isEqualTo(Result.create()
			.withDescription(null)
			.withRule("TEST_RULE")
			.withValue(NOT_APPLICABLE));
	}
}
