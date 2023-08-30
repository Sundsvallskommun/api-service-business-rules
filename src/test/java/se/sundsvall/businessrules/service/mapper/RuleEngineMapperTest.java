package se.sundsvall.businessrules.service.mapper;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.TestUtils;
import se.sundsvall.businessrules.TestUtils.TestCriteria;
import se.sundsvall.businessrules.TestUtils.TestRule;
import se.sundsvall.businessrules.TestUtils.TestRuleEngine;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.api.model.ResultDetail;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.rule.CriteriaResult;

class RuleEngineMapperTest {

	@Test
	void toFactMap() {

		// Arrange
		final var fact1 = Fact.create().withKey("a").withValue("a-value");
		final var fact2 = Fact.create().withKey("b").withValue("b-value");
		final var fact3 = Fact.create().withKey("c").withValue("c-value");
		final var factList = List.of(fact1, fact2, fact3);

		// Act
		final var result = RuleEngineMapper.toFactMap(factList);

		// Assert
		assertThat(result)
			.containsExactly(
				entry("a", fact1),
				entry("b", fact2),
				entry("c", fact3));
	}

	@Test
	void toStringMap() {

		// Arrange
		final var fact1 = Fact.create().withKey("a").withValue("a-value");
		final var fact2 = Fact.create().withKey("b").withValue("b-value");
		final var fact3 = Fact.create().withKey("c").withValue("c-value");
		final var factList = List.of(fact1, fact2, fact3);

		// Act
		final var result = RuleEngineMapper.toStringMap(factList);

		// Assert
		assertThat(result)
			.containsExactly(
				entry("a", "a-value"),
				entry("b", "b-value"),
				entry("c", "c-value"));
	}

	@Test
	void toMapWhenInputListIsNull() {

		// Act
		final var result = RuleEngineMapper.toFactMap(null);

		// Assert
		assertThat(result)
			.isNotNull()
			.isEmpty();
	}

	@Test
	void toResultWithOneFail() throws Exception {

		final var rule = new TestUtils.TestRule();
		final var criteria = new TestCriteria();
		final var criteriaList = List.of(
			new CriteriaResult(true, "OK", criteria),
			new CriteriaResult(false, "NOT OK", criteria));

		// Act
		final var result = RuleEngineMapper.toResult(rule, criteriaList);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDetails()).containsExactly(
			ResultDetail.create().withDescription("OK").withEvaluationValue(true).withOrigin("TEST_CRITERIA"),
			ResultDetail.create().withDescription("NOT OK").withEvaluationValue(false).withOrigin("TEST_CRITERIA"));
		assertThat(result.getValue()).isEqualTo(FAIL);
	}

	@Test
	void toResultWithAllFail() throws Exception {

		final var rule = new TestUtils.TestRule();
		final var criteria = new TestCriteria();
		final var criteriaList = List.of(
			new CriteriaResult(false, "NOT OK: 1", criteria),
			new CriteriaResult(false, "NOT OK: 2", criteria));

		// Act
		final var result = RuleEngineMapper.toResult(rule, criteriaList);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDetails()).containsExactly(
			ResultDetail.create().withDescription("NOT OK: 1").withEvaluationValue(false).withOrigin("TEST_CRITERIA"),
			ResultDetail.create().withDescription("NOT OK: 2").withEvaluationValue(false).withOrigin("TEST_CRITERIA"));
		assertThat(result.getValue()).isEqualTo(FAIL);
	}

	@Test
	void toResultWithAllPass() throws Exception {

		final var rule = new TestUtils.TestRule();
		final var criteria = new TestCriteria();
		final var criteriaList = List.of(
			new CriteriaResult(true, "OK: 1", criteria),
			new CriteriaResult(true, "OK: 2", criteria));

		// Act
		final var result = RuleEngineMapper.toResult(rule, criteriaList);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDetails()).containsExactly(
			ResultDetail.create().withDescription("OK: 1").withEvaluationValue(true).withOrigin("TEST_CRITERIA"),
			ResultDetail.create().withDescription("OK: 2").withEvaluationValue(true).withOrigin("TEST_CRITERIA"));
		assertThat(result.getValue()).isEqualTo(PASS);
	}

	@Test
	void toNonApplicableResult() {

		// Arrange
		final var rule = new TestRule();

		// Act
		final var result = RuleEngineMapper.toNonApplicableResult(rule);

		// Assert
		assertThat(result).isEqualTo(Result.create()
			.withDetails(null)
			.withRule("TEST_RULE")
			.withValue(NOT_APPLICABLE));
	}

	@Test
	void toValidationErrorResult() {

		// Arrange
		final var rule = new TestRule();
		final var validationErrors = List.of("Error 1", "Error 2");

		// Act
		final var result = RuleEngineMapper.toValidationErrorResult(rule, validationErrors);

		// Assert
		assertThat(result).isEqualTo(Result.create()
			.withDetails(List.of(
				ResultDetail.create().withDescription("Error 1"),
				ResultDetail.create().withDescription("Error 2")))
			.withRule("TEST_RULE")
			.withValue(VALIDATION_ERROR));
	}

	@Test
	void toRuleEngineResponse() {

		// Arrange
		final var ruleEngine = new TestRuleEngine();
		final var results = List.of(Result.create(), Result.create());

		// Act
		final var result = RuleEngineMapper.toRuleEngineResponse(ruleEngine, results);

		// Assert
		assertThat(result).isEqualTo(RuleEngineResponse.create()
			.withContext(PARKING_PERMIT.toString())
			.withResults(results));
	}
}
