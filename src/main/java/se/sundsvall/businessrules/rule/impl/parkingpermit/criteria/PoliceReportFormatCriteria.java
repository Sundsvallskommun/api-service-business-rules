package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toFactMap;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.hasValue;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.matches;

import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

/**
 * Criteria for valid police report format.
 *
 * Inputs:
 * application.lostPermit.policeReportNumber
 */
@Component
public class PoliceReportFormatCriteria implements Criteria {

	private static final String VALID_POLICE_REPORT_FORMAT = "^(5000)-(K\\w{7})-(2\\w)$"; // E.g.: 5000-KXXXXXXX-2X
	private static final String VALID_POLICE_REPORT_NUMBER = "polisrapportens diarienummer har korrekt format";
	private static final String INVALID_POLICE_REPORT_NUMBER = "polisrapportens diarienummer har felaktigt format";
	private static final String INVALID_POLICE_REPORT_MISSING = "diarienummer för polisrapport saknas";

	@Override
	public CriteriaResult evaluate(String municipalityId, List<Fact> facts) {

		// Input
		final var factMap = toFactMap(facts);
		final var policeReportNumber = factMap.get(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey());

		if (!hasValue(policeReportNumber)) {
			return new CriteriaResult(false, INVALID_POLICE_REPORT_MISSING, this);
		}
		if (!matches(policeReportNumber, VALID_POLICE_REPORT_FORMAT)) {
			return new CriteriaResult(false, INVALID_POLICE_REPORT_NUMBER, this);
		}

		return new CriteriaResult(true, VALID_POLICE_REPORT_NUMBER, this);
	}
}
