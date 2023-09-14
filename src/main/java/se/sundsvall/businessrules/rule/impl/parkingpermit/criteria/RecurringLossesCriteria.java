package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static generated.se.sundsvall.citizenassets.Status.BLOCKED;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.STATUS_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.STATUS_REASON_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for recurring losses.
 *
 * Inputs:
 * stakeholders.applicant.personid
 */
@Component
public class RecurringLossesCriteria implements Criteria {

	private static final String FIRST_TIME_OCCURRENCE = "det finns inga tidigare förlustanmälningar";
	private static final String RECURRING_LOSSES = "det finns tidigare förlustanmälningar";

	// CitizenAssets parameter constants.
	private static final String STATUS = BLOCKED.toString();
	private static final String STATUS_REASON = "LOST";
	private static final String TYPE = "PERMIT";

	@Autowired
	private CitizenAssetsClient citizenAssetsClient;

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var stakeHoldersApplicantPersonId = factMap.get(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey());

		if (hasRecuringLosses(stakeHoldersApplicantPersonId.getValue())) {
			return new CriteriaResult(false, RECURRING_LOSSES, this);
		}

		return new CriteriaResult(true, FIRST_TIME_OCCURRENCE, this);
	}

	private boolean hasRecuringLosses(String partyId) {
		if (isBlank(partyId)) {
			return false;
		}

		// Fetch all blocked parking-permits for this person.
		final var blockedParkingPermits = citizenAssetsClient.getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, STATUS,
			STATUS_REASON_PARAMETER, STATUS_REASON,
			TYPE_PARAMETER, TYPE));

		return isNotEmpty(blockedParkingPermits);
	}
}
