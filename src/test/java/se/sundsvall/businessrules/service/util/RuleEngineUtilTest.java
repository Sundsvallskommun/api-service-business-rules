package se.sundsvall.businessrules.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.Test;

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
}
