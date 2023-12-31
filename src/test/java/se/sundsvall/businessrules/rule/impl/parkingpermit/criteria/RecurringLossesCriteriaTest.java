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
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;

@ExtendWith(MockitoExtension.class)
class RecurringLossesCriteriaTest {
	private static final String PARTY_ID_PARAMETER = "partyId";
	private static final String TYPE_PARAMETER = "type";
	private static final String STATUS_PARAMETER = "status";
	private static final String STATUS_REASON_PARAMETER = "statusReason";

	@Mock
	private PartyAssetsClient partyAssetsClientMock;

	@InjectMocks
	private RecurringLossesCriteria criteria;

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
		assertThat(result.description()).isEqualTo("det finns inga tidigare förlustanmälningar");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, "BLOCKED",
			STATUS_REASON_PARAMETER, "LOST",
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
		assertThat(result.description()).isEqualTo("det finns tidigare förlustanmälningar");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, "BLOCKED",
			STATUS_REASON_PARAMETER, "LOST",
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}
}
