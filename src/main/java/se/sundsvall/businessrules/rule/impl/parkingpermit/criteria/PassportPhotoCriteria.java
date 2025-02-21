package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.PASSPORT_PHOTO_ATTACHMENT;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.matches;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for passport photo:
 *
 * Inputs: attachment.passportPhoto
 */
@Component
public class PassportPhotoCriteria implements Criteria {

	private static final String VALID_PASSPORT_PHOTO_VALUE = "^exists$";
	private static final String VALID_PASSPORT_PHOTO = "passfoto har skickats in";
	private static final String INVALID_PASSPORT_PHOTO = "passfoto har skickats in";

	@Override
	public CriteriaResult evaluate(String municipalityId, List<Fact> facts) {
		// Input
		final var factMap = toFactMap(facts);
		final var passportPhotoAttachment = factMap.get(PASSPORT_PHOTO_ATTACHMENT.getKey());

		// Evaluation
		if (matches(passportPhotoAttachment, VALID_PASSPORT_PHOTO_VALUE)) {
			return new CriteriaResult(true, VALID_PASSPORT_PHOTO, this);
		} else {
			return new CriteriaResult(false, INVALID_PASSPORT_PHOTO, this);
		}
	}
}
