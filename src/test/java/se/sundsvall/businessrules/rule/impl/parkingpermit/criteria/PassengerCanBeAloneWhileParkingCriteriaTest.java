package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

@ExtendWith(MockitoExtension.class)
class PassengerCanBeAloneWhileParkingCriteriaTest {

	@InjectMocks
	private PassengerCanBeAloneWhileParkingCriteria criteria;

	@Test
	void evaluateSuccessDueToApplicantCanNotBeLeftAlone() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("false"));

		// Act
		final var result = criteria.evaluate(facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande kan inte lämnas ensam en kort stund vid parkering");
	}

	@Test
	void evaluateFailureDueToApplicantCanBeLeftAlone() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("true"));

		// Act
		final var result = criteria.evaluate(facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande kan lämnas ensam en kort stund vid parkering");
	}
}
