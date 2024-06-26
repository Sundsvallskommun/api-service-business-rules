package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_DURATION;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class DurationCriteriaTest {

	@InjectMocks
	private DurationCriteria criteria;

	@Test
	void evaluateNewApplicationSuccessDueToDuration() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("funktionsnedsättningens varaktighet är 6 månader eller längre");
	}

	@Test
	void evaluateNewApplicationFailureDueToDuration() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P6M"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("funktionsnedsättningens varaktighet är kortare än 6 månader");
	}

	@Test
	void evaluateRenewalApplicationSuccessDueToDuration() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("funktionsnedsättningens varaktighet är 6 månader eller längre");
	}

	@Test
	void evaluateRenewalApplicationSuccessDueToNoChangedCircumstances() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES.getKey()).withValue("false"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P6M"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("det finns inga förändrade förutsättningar inför förnyelsen");
	}

	@Test
	void evaluateRenewalApplicationFailureDueToDuration() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P6M"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("funktionsnedsättningens varaktighet är kortare än 6 månader");
	}
}
