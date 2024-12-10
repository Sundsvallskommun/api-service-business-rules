package se.sundsvall.businessrules.service.util;

import static java.util.Objects.nonNull;

import java.util.UUID;
import se.sundsvall.businessrules.api.model.Fact;

public final class RuleEngineUtil {

	private RuleEngineUtil() {}

	public static boolean isValidUUID(String uuid) {
		try {
			return nonNull(uuid) && nonNull(UUID.fromString(uuid));
		} catch (final Exception e) {
			return false;
		}
	}

	public static boolean hasValidUUIDValue(Fact fact) {
		return nonNull(fact) && isValidUUID(fact.getValue());
	}

	public static boolean hasValidBooleanValue(Fact fact) {
		return nonNull(fact) && String.valueOf(fact.getValue()).matches("^(?i)(true|false)$");
	}

	public static boolean hasValidNumericValue(Fact fact) {
		return nonNull(fact) && String.valueOf(fact.getValue()).matches("^-?\\d+$");
	}

	public static boolean hasValue(Fact fact) {
		return nonNull(fact) && nonNull(fact.getValue());
	}

	public static int toInt(Fact fact) {
		return Integer.parseInt(fact.getValue());
	}

	public static boolean toBoolean(Fact fact) {
		return nonNull(fact) && Boolean.parseBoolean(fact.getValue());
	}

	public static boolean matches(Fact fact, String reqExp) {
		return nonNull(fact) && nonNull(fact.getValue()) && nonNull(reqExp) && fact.getValue().matches(reqExp);
	}
}
