package se.sundsvall.businessrules.service.util;

import static java.util.Objects.nonNull;

import java.util.UUID;

public class RuleEngineUtil {

	private RuleEngineUtil() {}

	public static boolean isValidUuid(String uuid) {
		try {
			return nonNull(uuid) && nonNull(UUID.fromString(uuid));
		} catch (final Exception e) {
			return false;
		}
	}
}
