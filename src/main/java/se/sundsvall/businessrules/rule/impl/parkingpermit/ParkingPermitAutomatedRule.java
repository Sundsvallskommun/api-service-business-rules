package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.MEDICAL_CONFIRMATION_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.PASSPORT_PHOTO_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ATTACHMENT;
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
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.MedicalConformationCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.NoActiveParkingPermitCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.PassportPhotoCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.SignatureCriteria;

@Component
public class ParkingPermitAutomatedRule implements ParkingPermitRuleAutomated {

	private final CriteriaEvaluator criteriaEvaluator;

	public ParkingPermitAutomatedRule(CriteriaEvaluator criteriaEvaluator) {
		this.criteriaEvaluator = criteriaEvaluator;
	}

	@Override
	public boolean isApplicable(List<Fact> facts) {
		// Input
		final var factMap = toStringMap(facts);
		final var type = factMap.get(TYPE.getKey());
		final var applicantCapacity = factMap.get(APPLICATION_APPLICANT_CAPACITY.getKey());

		return applicablePermit(type, applicantCapacity) || applicablePermitRenewal(type, applicantCapacity) || applicableLostPermit(type);
	}

	@Override
	public Result evaluate(String municipalityId, List<Fact> facts) {
		final var validationErrors = validateInput(facts);
		if (isNotEmpty(validationErrors)) {
			return toValidationErrorResult(this, validationErrors);
		}

		// Evaluate all criteria for this rule.
		return toResult(this, criteriaEvaluator.evaluateCriteriaComponent(this, municipalityId, facts));
	}

	@Override
	public List<Class<? extends Criteria>> getCriteria() {
		return List.of(NoActiveParkingPermitCriteria.class, MedicalConformationCriteria.class, PassportPhotoCriteria.class, SignatureCriteria.class);
	}

	private boolean applicablePermit(String type, String applicantCapacity) {
		return "PARKING_PERMIT".equals(type) && "DRIVER".equals(applicantCapacity) ||
			"PARKING_PERMIT".equals(type) && "PASSENGER".equals(applicantCapacity);
	}

	private boolean applicablePermitRenewal(String type, String applicantCapacity) {
		return "PARKING_PERMIT_RENEWAL".equals(type) && "DRIVER".equals(applicantCapacity) ||
			"PARKING_PERMIT_RENEWAL".equals(type) && "PASSENGER".equals(applicantCapacity);
	}

	private boolean applicableLostPermit(String type) {
		return "LOST_PARKING_PERMIT".equals(type);
	}

	private List<String> validateInput(List<Fact> facts) {

		final var factMap = toFactMap(facts);
		final var errorDescriptions = new ArrayList<String>();

		if (!hasValue(factMap.get(MEDICAL_CONFIRMATION_ATTACHMENT.getKey()))) {
			errorDescriptions.add(createErrorMessageForMissingFact(MEDICAL_CONFIRMATION_ATTACHMENT));
		}

		if (!hasValue(factMap.get(SIGNATURE_ATTACHMENT.getKey()))) {
			errorDescriptions.add(createErrorMessageForMissingFact(SIGNATURE_ATTACHMENT));
		}

		if (!hasValue(factMap.get(SIGNATURE_ABILITY.getKey()))) {
			errorDescriptions.add(createErrorMessageForMissingFact(SIGNATURE_ABILITY));
		}

		if (!hasValue(factMap.get(PASSPORT_PHOTO_ATTACHMENT.getKey()))) {
			errorDescriptions.add(createErrorMessageForMissingFact(PASSPORT_PHOTO_ATTACHMENT));
		}

		if (!hasValidUUIDValue(factMap.get(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()))) {
			errorDescriptions.add(createErrorMessageForMissingFact(STAKEHOLDERS_APPLICANT_PERSON_ID));
		}

		return errorDescriptions;
	}
}
