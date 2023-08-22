package se.sundsvall.businessrules.service.mapper;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.TestUtils;
import se.sundsvall.businessrules.TestUtils.TestRule;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.CriteriaResult;

class RuleEngineMapperTest {

	@Test
	void toMap() {

		// Arrange
		final var factList = List.of(
			Fact.create().withKey("a").withValue("a-value"),
			Fact.create().withKey("b").withValue("b-value"),
			Fact.create().withKey("c").withValue("c-value"));

		// Act
		final var result = RuleEngineMapper.toMap(factList);

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
		final var result = RuleEngineMapper.toMap(null);

		// Assert
		assertThat(result)
			.isNotNull()
			.isEmpty();
	}

	@Test
	void toResultWithOneFail() throws Exception {

		final var rule = new TestUtils.TestRule();
		final var criteriaList = List.of(
			new CriteriaResult(true, "OK"),
			new CriteriaResult(false, "NOT OK"));

		// Act
		final var result = RuleEngineMapper.toResult(rule, criteriaList);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescriptions()).containsExactly("OK", "NOT OK");
		assertThat(result.getValue()).isEqualTo(FAIL);
	}

	@Test
	void toResultWithAllFail() throws Exception {

		final var rule = new TestUtils.TestRule();
		final var criteriaList = List.of(
			new CriteriaResult(false, "NOT OK: 1"),
			new CriteriaResult(false, "NOT OK: 2"));

		// Act
		final var result = RuleEngineMapper.toResult(rule, criteriaList);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescriptions()).containsExactly("NOT OK: 1", "NOT OK: 2");
		assertThat(result.getValue()).isEqualTo(FAIL);
	}

	@Test
	void toResultWithAllPass() throws Exception {

		final var rule = new TestUtils.TestRule();
		final var criteriaList = List.of(
			new CriteriaResult(true, "OK: 1"),
			new CriteriaResult(true, "OK: 2"));

		// Act
		final var result = RuleEngineMapper.toResult(rule, criteriaList);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescriptions()).containsExactly("OK: 1", "OK: 2");
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
			.withDescriptions(null)
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
			.withDescriptions(validationErrors)
			.withRule("TEST_RULE")
			.withValue(VALIDATION_ERROR));
	}
}
