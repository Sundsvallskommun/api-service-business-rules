package se.sundsvall.businessrules.rule.parkingpermit.criteria;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY;
import static se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for driver walking ability.
 *
 * Inputs:
 * disability.walkingAbility (boolean)
 * disability.walkingDistance.max (int)
 */
public class DriverWalkingAbilityCriteria implements Criteria {

	private static final int MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL = 500;
	private static final String WALKING_ABILITY_NONE = "den sökande är helt rullstolsburen";
	private static final String WALKING_ABILITY_DONT_EXCEED_THRESHOLD_VALUE = format("den sökande kan inte gå längre än %s meter", MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL);
	private static final String WALKING_ABILITY_EXCEEDS_THRESHOLD_VALUE = format("den sökande kan gå längre än %s meter", MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL);

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var walkingAbility = factMap.get(DISABILITY_WALKING_ABILITY.getKey());
		final var maxWalkingDistance = factMap.get(DISABILITY_WALKING_DISTANCE_MAX.getKey());

		// Evaluation
		if (walkingAbility.hasBooleanValue() && !toBoolean(walkingAbility.getValue())) {
			return new CriteriaResult(true, WALKING_ABILITY_NONE, this);
		}
		if (maxWalkingDistance.hasNumericValue() && (parseInt(maxWalkingDistance.getValue()) <= MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL)) {
			return new CriteriaResult(true, WALKING_ABILITY_DONT_EXCEED_THRESHOLD_VALUE, this);
		}

		return new CriteriaResult(false, WALKING_ABILITY_EXCEEDS_THRESHOLD_VALUE, this);
	}
}
