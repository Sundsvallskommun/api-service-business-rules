package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ATTACHMENT;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValidBooleanValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.toBoolean;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for signature:
 *
 * Inputs: attachment.signature application.applicant.signingAbility
 */
@Component
public class SignatureCriteria implements Criteria {

	private static final String VALID_SIGNATURE = "signatur har skickats in";
	private static final String VALID_NO_SIGNATURE_NEEDED = "signatur behövs ej";
	private static final String INVALID_SIGNATURE = "signatur saknas";

	@Override
	public CriteriaResult evaluate(String municipalityId, List<Fact> facts) {
		// Input
		final var factMap = toFactMap(facts);
		final var signatureAttachment = factMap.get(SIGNATURE_ATTACHMENT.getKey());
		final var signatureAbility = factMap.get(SIGNATURE_ABILITY.getKey());

		// Evaluation
		if (hasValidBooleanValue(signatureAbility) && !toBoolean(signatureAbility)) {
			return new CriteriaResult(true, VALID_NO_SIGNATURE_NEEDED, this);
		} else if (hasValidBooleanValue(signatureAttachment) && toBoolean((signatureAttachment))) {
			return new CriteriaResult(true, VALID_SIGNATURE, this);
		} else {
			return new CriteriaResult(false, INVALID_SIGNATURE, this);
		}
	}
}
