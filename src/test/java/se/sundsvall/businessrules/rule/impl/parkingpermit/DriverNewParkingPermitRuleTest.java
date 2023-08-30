package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.ResultDetail;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
class DriverNewParkingPermitRuleTest {

	@SpyBean
	private CriteriaEvaluator criteriaEvaluatorSpy;

	@Autowired
	private DriverNewParkingPermitRule rule;

	@Test
	void isApplicableIsTrue() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
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
			Fact.create().withKey(TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("PASSENGER")); // Not valid for this rule.

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void evaluateSuccess() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("499"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_NEW_PARKING_PERMIT_RULE");
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
				.withOrigin("NO_ACTIVE_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga aktiva parkeringstillstånd")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, facts);
	}

	@Test
	void evaluateFailureDueToFailedCriteria() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getKey()).withValue("P6M"), // Will make the evaluation fail.
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("499"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("DRIVER_WALKING_ABILITY_CRITERIA")
				.withDescription("den sökande kan inte gå längre än 500 meter"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withOrigin("DURATION_CRITERIA")
				.withDescription("funktionsnedsättningens varaktighet är kortare än 6 månader"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("NO_ACTIVE_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga aktiva parkeringstillstånd")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, facts);
	}

	@Test
	void evaluateFailureDueToAllMandatoryParametersMissing() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_NEW_PARKING_PERMIT_RULE");
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
	}

	@Test
	void evaluateFailureDueToInvalidPersonId() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("499"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue("invalid-uuid")); // Will make validation fail.

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")

		));

		verifyNoInteractions(criteriaEvaluatorSpy);
	}

	@Test
	void evaluateFailureDueToMissingWalkingDistanceMax() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue(null), // Will make validation fail. Must be present when walkingAbility is true.
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingDistance.max' (uppgift om maximal gångsträcka för den sökande)")

		));

		verifyNoInteractions(criteriaEvaluatorSpy);
	}

	@Test
	void evaluateFailureDueToNonNumericValueInWalkingDistanceMax() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("PARKING_PERMIT"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).withValue("DRIVER"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getKey()).withValue("P1Y"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getKey()).withValue("non-numeric"), // Will make validation fail. Must be numeric value when walkingAbility is true.
			Fact.create().withKey(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(UUID.randomUUID().toString()));

		// Act
		final var result = rule.evaluate(facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("DRIVER_NEW_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'disability.walkingDistance.max' (uppgift om maximal gångsträcka för den sökande)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
	}
}