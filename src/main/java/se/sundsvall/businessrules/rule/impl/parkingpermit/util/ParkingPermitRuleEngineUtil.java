package se.sundsvall.businessrules.rule.impl.parkingpermit.util;

import static se.sundsvall.businessrules.service.Constants.ERROR_MESSAGE_MISSING_FACT;

import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.DurationEnum;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

public final class ParkingPermitRuleEngineUtil {

	private ParkingPermitRuleEngineUtil() {}

	public static String createErrorMessageForMissingFact(ParkingPermitFactKeyEnum fact) {
		return ERROR_MESSAGE_MISSING_FACT.formatted(fact.getKey(), fact.getDescription());
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
