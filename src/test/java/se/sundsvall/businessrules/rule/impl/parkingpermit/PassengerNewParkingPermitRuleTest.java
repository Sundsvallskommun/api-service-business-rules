package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.STATUS_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_CAN_BE_ALONE_WHILE_PARKING;
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

import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.ResultDetail;
import se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.DurationCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.NoActiveParkingPermitCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.PassengerCanBeAloneWhileParkingCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.PassengerWalkingAbilityCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class PassengerNewParkingPermitRuleTest {

	@MockBean
	private PartyAssetsClient partyAssetsClientMock;

	@SpyBean
	private CriteriaEvaluator criteriaEvaluatorSpy;

	@Autowired
	private PassengerNewParkingPermitRule rule;

	@Test
	void getCriteria() {
		assertThat(rule.getCriteria()).containsExactlyInAnyOrder(
			PassengerCanBeAloneWhileParkingCriteria.class,
			PassengerWalkingAbilityCriteria.class,
			DurationCriteria.class,
			NoActiveParkingPermitCriteria.class);
	}

	@Test
	void isApplicableIsTrue() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void isApplicableIsFalse() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER")); // Not valid for this rule.

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void evaluateSuccess() {

		// Arrange
		final var municipalityId = "2281";
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("false"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("99"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(PASS);
		assertThat(result.getDetails()).containsExactlyInAnyOrderElementsOf(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("PASSENGER_CAN_BE_ALONE_WHILE_PARKING_CRITERIA")
				.withDescription("den sökande kan inte lämnas ensam en kort stund vid parkering"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("PASSENGER_WALKING_ABILITY_CRITERIA")
				.withDescription("den sökande kan inte gå längre än 100 meter"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("DURATION_CRITERIA")
				.withDescription("funktionsnedsättningens varaktighet är 6 månader eller längre"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("NO_ACTIVE_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga aktiva parkeringstillstånd")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, municipalityId, facts);
		verify(partyAssetsClientMock).getAssets(Map.of(PARTY_ID_PARAMETER, partyId, STATUS_PARAMETER, "ACTIVE", TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureDueToFailedCriteria() {

		// Arrange
		final var municipalityId = "2281";
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P6M"), // Will make the evaluation fail.
			Fact.create().withKey(DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("false"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("99"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
		assertThat(result.getDetails()).containsExactlyInAnyOrderElementsOf(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("PASSENGER_CAN_BE_ALONE_WHILE_PARKING_CRITERIA")
				.withDescription("den sökande kan inte lämnas ensam en kort stund vid parkering"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("PASSENGER_WALKING_ABILITY_CRITERIA")
				.withDescription("den sökande kan inte gå längre än 100 meter"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withOrigin("DURATION_CRITERIA")
				.withDescription("funktionsnedsättningens varaktighet är kortare än 6 månader"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("NO_ACTIVE_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga aktiva parkeringstillstånd")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, municipalityId, facts);
		verify(partyAssetsClientMock).getAssets(Map.of(PARTY_ID_PARAMETER, partyId, STATUS_PARAMETER, "ACTIVE", TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureDueToAllMandatoryParametersMissing() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).containsExactlyInAnyOrderElementsOf(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.canBeAloneWhileParking' (upplysning ifall den sökande kan lämnas ensam eller ej under tiden bilen parkeras)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.duration' (funktionsnedsättningens varaktighet)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingAbility' (information om sökande har kapacitet att gå eller är rullstolsburen)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToInvalidPartyId() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("false"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("99"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue("invalid-uuid")); // Will make validation fail.

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToMissingCanBeAloneWhileParking() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("99"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.canBeAloneWhileParking' (upplysning ifall den sökande kan lämnas ensam eller ej under tiden bilen parkeras)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToMissingWalkingDistanceMax() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("false"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue(null), // Will make validation fail. Must be present when walkingAbility is true.
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingDistance.max' (uppgift om maximal gångsträcka för den sökande)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}

	@Test
	void evaluateFailureDueToNonNumericValueInWalkingDistanceMax() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER"),
			Fact.create().withKey(DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).withValue("false"),
			Fact.create().withKey(DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("non-numeric"), // Will make validation fail. Must be numeric value when walkingAbility is true.
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PASSENGER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingDistance.max' (uppgift om maximal gångsträcka för den sökande)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}
}
