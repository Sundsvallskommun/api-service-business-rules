package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.util.ParkingPermitRuleEngineUtil.createErrorMessageForMissingFact;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toResult;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toStringMap;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toValidationErrorResult;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValidUUIDValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.PoliceReportFormatCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.RecurringLossesCriteria;

@Component
public class LostParkingPermitRule implements ParkingPermitRule {

	@Autowired
	private CriteriaEvaluator criteriaEvaluator;

	@Override
	public List<Class<? extends Criteria>> getCriteria() {
		return List.of(PoliceReportFormatCriteria.class, RecurringLossesCriteria.class);
	}

	@Override
	public boolean isApplicable(List<Fact> facts) {

		// Input
		final var factMap = toStringMap(facts);
		final var type = factMap.get(TYPE.getKey());

		return "LOST_PARKING_PERMIT".equals(type);
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
		final var policeReportNumber = factMap.get(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey());
		final var stakeHoldersApplicantPersonId = factMap.get(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey());
		final var errorDescriptions = new ArrayList<String>();

		// Police report number must be present with valid value.
		if (!hasValue(policeReportNumber)) {
			errorDescriptions.add(createErrorMessageForMissingFact(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER));
		}

		// PersonId of applicant must be present as valid UUID.
		if (!hasValidUUIDValue(stakeHoldersApplicantPersonId)) {
			errorDescriptions.add(createErrorMessageForMissingFact(STAKEHOLDERS_APPLICANT_PERSON_ID));
		}

		return errorDescriptions;
	}
}
