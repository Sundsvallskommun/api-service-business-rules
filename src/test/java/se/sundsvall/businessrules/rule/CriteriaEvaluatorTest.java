package se.sundsvall.businessrules.rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.sundsvall.businessrules.TestUtils.TestCriteria;
import se.sundsvall.businessrules.TestUtils.TestRule;
import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class CriteriaEvaluatorTest {

	@InjectMocks
	private CriteriaEvaluator criteriaEvaluator;

	@Test
	void evaluateCriteriaComponent() {

		// Arrange
		ReflectionTestUtils.setField(criteriaEvaluator, "criteria", List.of(new TestCriteria()));
		final var municipalityId = "2281";
		final var rule = new TestRule();
		final var facts = List.of(
			Fact.create().withKey("key").withValue("value"));

		// Act
		final var result = criteriaEvaluator.evaluateCriteriaComponent(rule, municipalityId, facts);

		// Assert
		assertThat(result)
			.extracting(CriteriaResult::value, CriteriaResult::description)
			.containsExactly(tuple(true, "OK"));
	}

	@Test
	void evaluateComponent() {

		// Arrange
		final var municipalityId = "2281";
		final var rule = new TestRule();
		final var facts = List.of(
			Fact.create().withKey("key").withValue("value"));

		// Act
		final var result = CriteriaEvaluator.evaluateCriteria(rule, municipalityId, facts);

		// Assert
		assertThat(result)
			.extracting(CriteriaResult::value, CriteriaResult::description)
			.containsExactly(tuple(true, "OK"));
	}
}
