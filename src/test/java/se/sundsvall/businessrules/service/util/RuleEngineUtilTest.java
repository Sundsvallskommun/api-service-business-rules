package se.sundsvall.businessrules.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import se.sundsvall.businessrules.TestUtils.TestRule;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.CriteriaResult;

class RuleEngineUtilTest {

	@Test
	void evaluateCriteria() {

		// Arrange
		final var rule = new TestRule();
		final var facts = List.of(
			Fact.create().withKey("key").withValue("value"));

		// Act
		final var result = RuleEngineUtil.evaluateCriteria(rule, facts);

		// Assert
		assertThat(result)
			.extracting(CriteriaResult::value, CriteriaResult::description)
			.containsExactly(tuple(true, "OK"));
	}

	@ParameterizedTest
	@MethodSource("isValidUuidArguments")
	void isValidUuid(String uuid, boolean expectedResult) {
		assertThat(RuleEngineUtil.isValidUuid(uuid)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> isValidUuidArguments() {
		return Stream.of(
			Arguments.of(UUID.randomUUID().toString(), true),
			Arguments.of("not-valid-uuid", false),
			Arguments.of("", false),
			Arguments.of(null, false));
	}
}
