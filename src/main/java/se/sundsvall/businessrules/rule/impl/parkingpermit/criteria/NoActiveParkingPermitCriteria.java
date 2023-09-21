package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static generated.se.sundsvall.citizenassets.Status.ACTIVE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.STATUS_PARAMETER;
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
 * Criteria for no other active parking permits.
 *
 * Inputs:
 * stakeholders.applicant.personid
 */
@Component
public class NoActiveParkingPermitCriteria implements Criteria {

	private static final String NO_ACTIVE_PARKING_PERMIT_EXISTS = "den sökande har inga aktiva parkeringstillstånd";
	private static final String ACTIVE_PARKING_PERMIT_EXISTS = "den sökande har redan ett aktivt parkeringstillstånd";

	// CitizenAssets parameter constants.
	private static final String STATUS = ACTIVE.toString();
	private static final String TYPE = "PARKINGPERMIT";

	@Autowired
	private CitizenAssetsClient citizenAssetsClient;

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var stakeHoldersApplicantPersonId = factMap.get(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey());

		if (hasActiveParkingPermit(stakeHoldersApplicantPersonId.getValue())) {
			return new CriteriaResult(false, ACTIVE_PARKING_PERMIT_EXISTS, this);
		}
		return new CriteriaResult(true, NO_ACTIVE_PARKING_PERMIT_EXISTS, this);
	}

	private boolean hasActiveParkingPermit(String partyId) {
		if (isBlank(partyId)) {
			return false;
		}

		// Fetch all active parking-permits for this person.
		final var activeParkingPermits = citizenAssetsClient.getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, STATUS,
			TYPE_PARAMETER, TYPE));

		return isNotEmpty(activeParkingPermits);
	}
}
