package se.sundsvall.businessrules.rule.parkingpermit.util;

import static java.lang.String.format;
import static se.sundsvall.businessrules.service.Constants.ERROR_MESSAGE_MISSING_FACT;

import se.sundsvall.businessrules.rule.parkingpermit.enums.ParkingPermitFactKeyEnum;

public class ParkingPermitRuleEngineUtil {

	private ParkingPermitRuleEngineUtil() {}

	public static String createErrorMessageForMissingFact(ParkingPermitFactKeyEnum fact) {
		return format(ERROR_MESSAGE_MISSING_FACT, fact.getKey(), fact.getDescription());
	}
}
