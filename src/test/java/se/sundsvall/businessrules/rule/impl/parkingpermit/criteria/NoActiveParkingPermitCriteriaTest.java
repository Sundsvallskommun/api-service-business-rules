package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import generated.se.sundsvall.partyassets.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.STATUS_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;

@ExtendWith(MockitoExtension.class)
class NoActiveParkingPermitCriteriaTest {

	@Mock
	private PartyAssetsClient partyAssetsClientMock;

	@InjectMocks
	private NoActiveParkingPermitCriteria criteria;

	@Test
	void evaluateSuccess() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		when(partyAssetsClientMock.getAssets(any())).thenReturn(emptyList());

		// Act
		final var result = criteria.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande har inga aktiva parkeringstillstånd");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, "ACTIVE",
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailure() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		when(partyAssetsClientMock.getAssets(any())).thenReturn(List.of(new Asset()));

		// Act
		final var result = criteria.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande har redan ett aktivt parkeringstillstånd");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, "ACTIVE",
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}
}
