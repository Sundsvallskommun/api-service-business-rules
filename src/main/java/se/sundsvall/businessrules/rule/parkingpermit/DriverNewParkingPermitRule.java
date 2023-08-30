package se.sundsvall.businessrules.rule.parkingpermit;

import static com.nimbusds.oauth2.sdk.util.CollectionUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_DURATION;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;
import static se.sundsvall.businessrules.rule.parkingpermit.util.ParkingPermitRuleEngineUtil.createErrorMessageForMissingFact;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toResult;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toStringMap;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toValidationErrorResult;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.isValidUuid;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.ParkingPermitRule;
import se.sundsvall.businessrules.rule.parkingpermit.criteria.DriverWalkingAbilityCriteria;
import se.sundsvall.businessrules.rule.parkingpermit.criteria.DurationCriteria;

@Component
public class DriverNewParkingPermitRule implements ParkingPermitRule {

	@Autowired
	private CriteriaEvaluator criteriaEvaluator;

	@Override
	public List<Class<? extends Criteria>> getCriteria() {
		return List.of(DriverWalkingAbilityCriteria.class, DurationCriteria.class);
	}

	@Override
	public boolean isApplicable(List<Fact> facts) {

		// Input
		final var factMap = toStringMap(facts);
		final var type = factMap.get(TYPE.getKey());
		final var applicantCapacity = factMap.get(APPLICATION_APPLICANT_CAPACITY.getKey());

		return "PARKING_PERMIT".equals(type) && "DRIVER".equals(applicantCapacity);
	}

	@Override
	public Result evaluate(List<Fact> facts) {

		final var validationErrors = validateInput(facts);
		if (isNotEmpty(validationErrors)) {
			return toValidationErrorResult(this, validationErrors);
		}

		// Evaluate all criteria for this rule.
		return toResult(this, criteriaEvaluator.evaluateCriteriaComponent(this, facts));
	}

	private List<String> validateInput(List<Fact> facts) {

		final var factMap = toFactMap(facts);
		final var disabilityDuration = factMap.get(DISABILITY_DURATION.getKey());
		final var walkingAbility = factMap.get(DISABILITY_WALKING_ABILITY.getKey());
		final var disabilityWalkingDistanceMax = factMap.get(DISABILITY_WALKING_DISTANCE_MAX.getKey());
		final var stakeHoldersApplicantPersonId = factMap.get(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey());

		final var errorDescriptions = new ArrayList<String>();

		if (isNull(disabilityDuration)) { // Duration must be present with valid value.
			errorDescriptions.add(createErrorMessageForMissingFact(DISABILITY_DURATION));
		}
		if (isNull(walkingAbility) || !walkingAbility.hasBooleanValue()) { // WalkingAbility must be present as valid boolean.
			errorDescriptions.add(createErrorMessageForMissingFact(DISABILITY_WALKING_ABILITY));
		}
		if (isNull(disabilityWalkingDistanceMax) || !disabilityWalkingDistanceMax.hasNumericValue()) { // If applicant has walkingAbility, maxWalkingDistance must be present as numeric value.
			errorDescriptions.add(createErrorMessageForMissingFact(DISABILITY_WALKING_DISTANCE_MAX));
		}
		if (!isValidUuid(stakeHoldersApplicantPersonId.getValue())) { // PersonId of applicant must be present as valid uuid
			errorDescriptions.add(createErrorMessageForMissingFact(STAKEHOLDERS_APPLICANT_PERSON_ID));
		}

		return errorDescriptions;
	}
}
