package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.DurationEnum.LESS_THAN_6_MONTHS;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.DISABILITY_DURATION;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;

import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for disability duration.
 *
 * Inputs:
 * disability.duration (string, https://en.wikipedia.org/wiki/ISO_8601#Durations)
 * application.renewal.changedCircumstances (boolean)
 */
@Component
public class DurationCriteria implements Criteria {

	private static final String RENEWAL_WITH_UNCHANGED_CIRCUMSTANCES = "det finns inga förändrade förutsättningar inför förnyelsen";
	private static final String DURATION_LESS_THAN_THRESHOLD_VALUE = "funktionsnedsättningens varaktighet är kortare än 6 månader";
	private static final String DURATION_MORE_THAN_THRESHOLD_VALUE = "funktionsnedsättningens varaktighet är 6 månader eller längre";

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var disabilityDuration = factMap.get(DISABILITY_DURATION.getKey());
		final var changedCircumstances = factMap.get(APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES.getKey());

		// Parameter is only present when applying for renewal of permit.
		if (nonNull(changedCircumstances) && changedCircumstances.hasBooleanValue()) {
			return evaluateRenewalApplication(toBoolean(changedCircumstances.getValue()), disabilityDuration.getValue());
		}
		return evaluateNewApplication(disabilityDuration.getValue());
	}

	private CriteriaResult evaluateRenewalApplication(final boolean changedCircumstances, final String disabilityDuration) {
		if (changedCircumstances && LESS_THAN_6_MONTHS.isEqualTo(disabilityDuration)) {
			return new CriteriaResult(false, DURATION_LESS_THAN_THRESHOLD_VALUE, this);
		}

		return new CriteriaResult(true, LESS_THAN_6_MONTHS.isEqualTo(disabilityDuration) ? RENEWAL_WITH_UNCHANGED_CIRCUMSTANCES : DURATION_MORE_THAN_THRESHOLD_VALUE, this);
	}

	private CriteriaResult evaluateNewApplication(final String disabilityDuration) {
		if (LESS_THAN_6_MONTHS.isEqualTo(disabilityDuration)) {
			return new CriteriaResult(false, DURATION_LESS_THAN_THRESHOLD_VALUE, this);
		}

		return new CriteriaResult(true, DURATION_MORE_THAN_THRESHOLD_VALUE, this);
	}
}
