package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_CAN_BE_ALONE_WHILE_PARKING;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValidBooleanValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.toBoolean;

import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria if passenger can be left alone or not during parking.
 *
 * Inputs:
 * disability.canBeAloneWhileParking (boolean)
 */
@Component
public class PassengerCanBeAloneWhileParkingCriteria implements Criteria {

	private static final String APPLICANT_CAN_BE_ALONE = "den sökande kan lämnas ensam en kort stund vid parkering";
	private static final String APPLICANT_CANT_BE_ALONE = "den sökande kan inte lämnas ensam en kort stund vid parkering";

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var canBeAloneWhileParking = factMap.get(DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey());

		// Evaluation
		if (hasValidBooleanValue(canBeAloneWhileParking) && !toBoolean((canBeAloneWhileParking))) {
			return new CriteriaResult(true, APPLICANT_CANT_BE_ALONE, this);
		}

		return new CriteriaResult(false, APPLICANT_CAN_BE_ALONE, this);
	}
}
