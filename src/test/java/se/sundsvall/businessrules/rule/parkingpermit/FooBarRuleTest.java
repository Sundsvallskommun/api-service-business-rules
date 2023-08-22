package se.sundsvall.businessrules.rule.parkingpermit;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class FooBarRuleTest {

	@InjectMocks
	private FooBarRule rule;

	@Test
	void isApplicableTrue() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey("foo").withValue(""),
			Fact.create().withKey("bar").withValue(""));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void isApplicableFalse() {

		// Arrange
		final var facts = List.of(Fact.create().withKey("xx").withValue("yy"));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void validationFails() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey("foo").withValue("not-a-boolean"),
			Fact.create().withKey("bar").withValue("not-a-boolean"));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescriptions()).isEqualTo(List.of(
			"foo.value is not a boolean value (must be true or false only)",
			"bar.value is not a boolean value (must be true or false only)"));
		assertThat(result.getRule()).isEqualTo("FOO_BAR_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
	}

	@Test
	void evaluateIsTrue() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey("foo").withValue("true"),
			Fact.create().withKey("bar").withValue("true"));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescriptions()).isEqualTo(List.of(
			"OK: foo is true",
			"OK: bar is true"));
		assertThat(result.getRule()).isEqualTo("FOO_BAR_RULE");
		assertThat(result.getValue()).isEqualTo(PASS);
	}

	@Test
	void evaluateIsFalse() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey("foo").withValue("false"),
			Fact.create().withKey("bar").withValue("false"));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescriptions()).isEqualTo(List.of(
			"FAIL: foo is not true",
			"FAIL: bar is not true"));
		assertThat(result.getRule()).isEqualTo("FOO_BAR_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
	}
}
