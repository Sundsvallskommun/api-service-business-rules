package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static generated.se.sundsvall.citizenassets.Status.EXPIRED;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_DURATION;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import generated.se.sundsvall.citizenassets.Asset;
import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.ResultDetail;
import se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.DriverWalkingAbilityCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.DurationCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.ExpiringParkingPermitCriteria;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class DriverRenewalParkingPermitRuleTest {

	@MockBean
	private CitizenAssetsClient citizenAssetsClientMock;

	@SpyBean
	private CriteriaEvaluator criteriaEvaluatorSpy;

	@Autowired
	private DriverRenewalParkingPermitRule rule;

	@Test
	void getCriteria() {
		assertThat(rule.getCriteria()).containsExactly(
			DriverWalkingAbilityCriteria.class,
			DurationCriteria.class,
			ExpiringParkingPermitCriteria.class);
	}

	@Test
	void isApplicableIsTrue() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void isApplicableIsFalse() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER")); // Not valid for this rule.

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void evaluateSuccess() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("499"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		// All previous permits are expired.
		when(citizenAssetsClientMock.getAssets(any())).thenReturn(List.of(
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED),
			new Asset().partyId(PARTY_ID_PARAMETER).status(EXPIRED)));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_RENEWAL_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(PASS);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("DRIVER_WALKING_ABILITY_CRITERIA")
				.withDescription("den sökande kan inte gå längre än 500 meter"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("DURATION_CRITERIA")
				.withDescription("funktionsnedsättningens varaktighet är 6 månader eller längre"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("EXPIRING_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har parkeringstillstånd som strax upphör eller redan har upphört")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, facts);
		verify(citizenAssetsClientMock).getAssets(Map.of(PARTY_ID_PARAMETER, partyId, TYPE_PARAMETER, "PERMIT"));
	}

	@Test
	void evaluateFailureDueToFailedCriteria() {

		// Arrange
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("499"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		// No parking permits at all. Will make EXPIRING_PARKING_PERMIT_CRITERIA to fail.
		when(citizenAssetsClientMock.getAssets(any())).thenReturn(emptyList());

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_RENEWAL_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("DRIVER_WALKING_ABILITY_CRITERIA")
				.withDescription("den sökande kan inte gå längre än 500 meter"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("DURATION_CRITERIA")
				.withDescription("funktionsnedsättningens varaktighet är 6 månader eller längre"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withOrigin("EXPIRING_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga parkeringstillstånd som är på väg att upphöra eller redan har upphört")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, facts);
		verify(citizenAssetsClientMock).getAssets(Map.of(PARTY_ID_PARAMETER, partyId, TYPE_PARAMETER, "PERMIT"));
	}

	@Test
	void evaluateFailureDueToAllMandatoryParametersMissing() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_RENEWAL_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.duration' (funktionsnedsättningens varaktighet)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingAbility' (information om sökande har kapacitet att gå eller är rullstolsburen)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")

		));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(citizenAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToInvalidPartyId() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("499"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue("invalid-uuid")); // Will make validation fail.

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_RENEWAL_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(citizenAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToMissingWalkingDistanceMax() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue(null), // Will make validation fail. Must be present when walkingAbility is true.
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_RENEWAL_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingDistance.max' (uppgift om maximal gångsträcka för den sökande)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(citizenAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToNonNumericValueInWalkingDistanceMax() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT_RENEWAL"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("non-numeric"), // Will make validation fail. Must be numeric value when walkingAbility is true.
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_RENEWAL_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingDistance.max' (uppgift om maximal gångsträcka för den sökande)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(citizenAssetsClientMock);
	}
}
