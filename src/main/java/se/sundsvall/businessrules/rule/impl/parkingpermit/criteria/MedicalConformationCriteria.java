package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.MEDICAL_CONFIRMATION_ATTACHMENT;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValidBooleanValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.toBoolean;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for medical conformation:
 *
 * Inputs: attachment.medicalConfirmation
 */
@Component
public class MedicalConformationCriteria implements Criteria {

	private static final String VALID_MEDICAL_CONFIRMATION = "läkarintyg har skickats in";
	private static final String INVALID_MEDICAL_CONFIRMATION = "läkarintyg saknas";

	@Override
	public CriteriaResult evaluate(String municipalityId, List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var medicalConfirmationAttachment = factMap.get(MEDICAL_CONFIRMATION_ATTACHMENT.getKey());

		// Evaluation
		if (hasValidBooleanValue(medicalConfirmationAttachment) && toBoolean((medicalConfirmationAttachment))) {
			return new CriteriaResult(true, VALID_MEDICAL_CONFIRMATION, this);
		}
		return new CriteriaResult(false, INVALID_MEDICAL_CONFIRMATION, this);
	}
}
