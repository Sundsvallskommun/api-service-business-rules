package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValidBooleanValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValidNumericValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.toBoolean;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.toInt;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for passenger walking ability.
 *
 * Inputs:
 * disability.walkingAbility (boolean)
 * disability.walkingDistance.max (int)
 */
@Component
public class PassengerWalkingAbilityCriteria implements Criteria {

	private static final int MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL = 100;
	private static final String WALKING_ABILITY_NONE = "den sökande är helt rullstolsburen";
	private static final String WALKING_ABILITY_DONT_EXCEED_THRESHOLD_VALUE = "den sökande kan inte gå längre än %s meter".formatted(MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL);
	private static final String WALKING_ABILITY_EXCEEDS_THRESHOLD_VALUE = "den sökande kan gå längre än %s meter".formatted(MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL);

	@Override
	public CriteriaResult evaluate(String municipalityId, List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var walkingAbility = factMap.get(DISABILITY_WALKING_ABILITY.getKey());
		final var maxWalkingDistance = factMap.get(DISABILITY_WALKING_DISTANCE_MAX.getKey());

		// Evaluation
		if (hasValidBooleanValue(walkingAbility) && !toBoolean((walkingAbility))) {
			return new CriteriaResult(true, WALKING_ABILITY_NONE, this);
		}
		if (hasValidNumericValue(maxWalkingDistance) && (toInt(maxWalkingDistance) <= MAXIMUM_WALKING_DISTANCE_FOR_APPROVAL)) {
			return new CriteriaResult(true, WALKING_ABILITY_DONT_EXCEED_THRESHOLD_VALUE, this);
		}

		return new CriteriaResult(false, WALKING_ABILITY_EXCEEDS_THRESHOLD_VALUE, this);
	}
}
