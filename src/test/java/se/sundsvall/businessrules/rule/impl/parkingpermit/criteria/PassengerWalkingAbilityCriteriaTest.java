package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class PassengerWalkingAbilityCriteriaTest {

	@InjectMocks
	private PassengerWalkingAbilityCriteria criteria;

	@Test
	void evaluateSuccessDueToWalkingDistance() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("99"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande kan inte gå längre än 100 meter");
	}

	@Test
	void evaluateSuccessDueToWalkingAbility() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("false"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande är helt rullstolsburen");
	}

	@Test
	void evaluateFailureDueToWalkingDistance() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("101"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande kan gå längre än 100 meter");
	}
}
