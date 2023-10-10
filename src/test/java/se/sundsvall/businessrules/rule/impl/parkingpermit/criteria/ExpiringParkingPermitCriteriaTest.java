package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import generated.se.sundsvall.partyassets.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static generated.se.sundsvall.partyassets.Status.ACTIVE;
import static generated.se.sundsvall.partyassets.Status.EXPIRED;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;

@ExtendWith(MockitoExtension.class)
class ExpiringParkingPermitCriteriaTest {

	private static final int EXPIRATION_PERIOD_IN_MONTHS = 2;

	@Mock
	private PartyAssetsClient partyAssetsClientMock;

	@InjectMocks
	private ExpiringParkingPermitCriteria criteria;

	@Test
	void evaluateSuccessAllPermitsHasExpired() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		when(partyAssetsClientMock.getAssets(any())).thenReturn(List.of(
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED),
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED),
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED)));

		// Act
		final var result = criteria.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande har parkeringstillstånd som strax upphör eller redan har upphört");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateSuccessActivePermitAboutToExpire() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		when(partyAssetsClientMock.getAssets(any())).thenReturn(List.of(
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED),
			new Asset().partyId(PARTY_ID_PARAMETER).status(ACTIVE).validTo(LocalDate.now().plusMonths(EXPIRATION_PERIOD_IN_MONTHS).minusDays(1)),
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED)));

		// Act
		final var result = criteria.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("den sökande har parkeringstillstånd som strax upphör eller redan har upphört");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureNoExisitingPermits() {

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
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande har inga parkeringstillstånd som är på väg att upphöra eller redan har upphört");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureNoExpiredPermits() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		when(partyAssetsClientMock.getAssets(any())).thenReturn(List.of(
			new Asset().partyId(PARTY_ID_PARAMETER).status(ACTIVE)));

		// Act
		final var result = criteria.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande har inga parkeringstillstånd som är på väg att upphöra eller redan har upphört");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureNoActivePermitAboutToExpire() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		when(partyAssetsClientMock.getAssets(any())).thenReturn(List.of(
			new Asset().partyId(PARTY_ID_PARAMETER).status(ACTIVE).validTo(LocalDate.now().plusMonths(EXPIRATION_PERIOD_IN_MONTHS).plusDays(1)))); // 1 day more than the grace period.

		// Act
		final var result = criteria.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("den sökande har inga parkeringstillstånd som är på väg att upphöra eller redan har upphört");

		verify(partyAssetsClientMock).getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}
}
