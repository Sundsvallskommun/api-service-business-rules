package se.sundsvall.businessrules.service.mapper;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.api.model.Fact;

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
}
