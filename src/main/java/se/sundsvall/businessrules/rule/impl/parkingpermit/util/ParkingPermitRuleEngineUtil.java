package se.sundsvall.businessrules.rule.impl.parkingpermit.util;

import static java.lang.String.format;
import static se.sundsvall.businessrules.service.Constants.ERROR_MESSAGE_MISSING_FACT;

import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.DurationEnum;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

public class ParkingPermitRuleEngineUtil {

	private ParkingPermitRuleEngineUtil() {}

	public static String createErrorMessageForMissingFact(ParkingPermitFactKeyEnum fact) {
		return format(ERROR_MESSAGE_MISSING_FACT, fact.getKey(), fact.getDescription());
	}

	public static boolean isValidDurationEnumValue(String duration) {
		try {
			DurationEnum.fromDuration(duration);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}
