package se.sundsvall.businessrules.rule.impl.parkingpermit.citeria;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.STATUS_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.TYPE_PARAMETER;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import generated.se.sundsvall.citizenassets.Asset;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.NoActiveParkingPermitCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

@ExtendWith(MockitoExtension.class)
class NoActiveParkingPermitCriteriaTest {

	@Mock
	private CitizenAssetsClient citizenAssetsClient;

	@InjectMocks
	private NoActiveParkingPermitCriteria criteria;

	@Test
	void evaluateSuccess() {

		// Arrange
		final var personId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(personId));

		when(citizenAssetsClient.getAssets(any())).thenReturn(emptyList());

		// Act
		final var result = criteria.evaluate(facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande har inga aktiva parkeringstillstånd");

		verify(citizenAssetsClient).getAssets(Map.of(
			PARTY_ID_PARAMETER, personId,
			STATUS_PARAMETER, "ACTIVE",
			TYPE_PARAMETER, "PERMIT"));
	}

	@Test
	void evaluateFailure() {

		// Arrange
		final var personId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(personId));

		when(citizenAssetsClient.getAssets(any())).thenReturn(List.of(new Asset()));

		// Act
		final var result = criteria.evaluate(facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande har redan ett aktivt parkeringstillstånd");

		verify(citizenAssetsClient).getAssets(Map.of(
			PARTY_ID_PARAMETER, personId,
			STATUS_PARAMETER, "ACTIVE",
			TYPE_PARAMETER, "PERMIT"));
	}

}
