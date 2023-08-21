package se.sundsvall.businessrules.service.util;

import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;

import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.Rule;

public class RuleEngineUtil {

	private RuleEngineUtil() {}

	public static Result nonApplicableResult(Rule rule) {
		return Result.create()
			.withRule(rule.getName())
			.withValue(NOT_APPLICABLE);
	}
}
