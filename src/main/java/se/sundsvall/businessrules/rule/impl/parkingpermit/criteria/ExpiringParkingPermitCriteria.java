package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static generated.se.sundsvall.citizenassets.Status.ACTIVE;
import static generated.se.sundsvall.citizenassets.Status.EXPIRED;
import static java.time.LocalDate.MAX;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import generated.se.sundsvall.citizenassets.Asset;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.integration.citizenassets.CitizenAssetsClient;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for expired or almost expired parking permits.
 *
 * Inputs:
 * stakeholders.applicant.personid
 */
@Component
public class ExpiringParkingPermitCriteria implements Criteria {

	private static final String HAS_EXPIRING_PARKING_PERMITS = "den sökande har parkeringstillstånd som strax upphör eller redan har upphört";
	private static final String HAS_NO_EXPIRING_PARKING_PERMITS = "den sökande har inga parkeringstillstånd som är på väg att upphöra eller redan har upphört";
	static final int EXPIRATION_PERIOD_IN_MONTHS = 2;

	// CitizenAssets parameter constants.
	private static final String TYPE = "PERMIT";

	@Autowired
	private CitizenAssetsClient citizenAssetsClient;

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var stakeHoldersApplicantPersonId = factMap.get(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey());

		if (hasExpiringParkingPermit(stakeHoldersApplicantPersonId.getValue())) {
			return new CriteriaResult(true, HAS_EXPIRING_PARKING_PERMITS, this);
		}
		return new CriteriaResult(false, HAS_NO_EXPIRING_PARKING_PERMITS, this);
	}

	private boolean hasExpiringParkingPermit(String partyId) {
		if (isBlank(partyId)) {
			return false;
		}

		// Fetch all issued parking-permits for this person.
		final var parkingPermits = citizenAssetsClient.getAssets(Map.of(
			PARTY_ID_PARAMETER, partyId,
			TYPE_PARAMETER, TYPE));

		// Return if all parking permits are expired or if the active permit is about to expire.
		return allPermitsHasExpired(parkingPermits) || activePermitAboutToExpire(parkingPermits);
	}

	private boolean allPermitsHasExpired(List<Asset> parkingPermits) {
		if (isEmpty(parkingPermits)) {
			return false;
		}

		return parkingPermits.stream()
			.map(Asset::getStatus)
			.allMatch(EXPIRED::equals);
	}

	private boolean activePermitAboutToExpire(List<Asset> parkingPermits) {
		return ofNullable(parkingPermits).orElse(emptyList()).stream()
			.filter(permit -> ACTIVE.equals(permit.getStatus()))
			.anyMatch(ExpiringParkingPermitCriteria::withinExpirationRange);
	}

	private static boolean withinExpirationRange(Asset parkingPermit) {
		final var expirationDate = ofNullable(parkingPermit.getValidTo())
			.orElse(MAX); // Empty validTo is interpreted as never expiring parking permit

		return expirationDate.isBefore(now().plusMonths(EXPIRATION_PERIOD_IN_MONTHS));
	}
}
