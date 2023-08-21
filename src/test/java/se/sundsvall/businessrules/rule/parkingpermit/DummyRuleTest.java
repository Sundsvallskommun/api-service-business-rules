package se.sundsvall.businessrules.rule.parkingpermit;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class DummyRuleTest {

	@InjectMocks
	private DummyRule rule;

	@Test
	void isApplicableIsTrue() {

		// Arrange
		final var facts = List.of(Fact.create().withKey("x").withValue("y"));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void isApplicableIsFalse() {

		// Arrange
		final var facts = List.of(Fact.create().withKey("xx").withValue("yy"));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void validateIsTrue() {

		// Arrange
		final var facts = List.of(Fact.create().withKey("x").withValue("y"));

		// Act
		final var result = rule.validate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescription()).isEqualTo("OK");
		assertThat(result.getRule()).isEqualTo("DUMMY_RULE");
		assertThat(result.getValue()).isEqualTo(PASS);
	}

	@Test
	void validateIsFalse() {

		// Arrange
		final var facts = List.of(Fact.create().withKey("xx").withValue("yy"));

		// Act
		final var result = rule.validate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getDescription()).isEqualTo("Missing valid value for x!");
		assertThat(result.getRule()).isEqualTo("DUMMY_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
	}
}
