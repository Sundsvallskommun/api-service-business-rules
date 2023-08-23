package se.sundsvall.businessrules.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.rule.CriteriaResult;
import se.sundsvall.businessrules.rule.Rule;
import se.sundsvall.businessrules.service.engine.RuleEngine;

public class RuleEngineMapper {

	private RuleEngineMapper() {}

	/**
	 * Convert a List of facts to a Map.
	 *
	 * @param  facts the fact List
	 * @return       a map of facts.
	 */
	public static Map<String, String> toMap(List<Fact> facts) {
		return Optional.ofNullable(facts).orElse(emptyList()).stream()
			.filter(fact -> nonNull(fact.getKey()))
			.collect(Collectors.toMap(Fact::getKey, Fact::getValue));
	}

	/**
	 * Converts a rule and a List of criteria results to a Result object.
	 *
	 * @param  rule            the rule that generated the criteria results.
	 * @param  criteriaResults a List of criteria.
	 * @return                 a Result object.
	 */
	public static Result toResult(Rule rule, List<CriteriaResult> criteriaResults) {
		return Result.create()
			.withDescriptions(Optional.ofNullable(criteriaResults).orElse(emptyList()).stream()
				.map(CriteriaResult::description)
				.toList())
			.withRule(rule.getName())
			.withValue(allPass(criteriaResults) ? PASS : FAIL);
	}

	/**
	 * Evaluates all criteria results. If all results have positive (true) values, this
	 * method returns true, otherwise false.
	 *
	 * @param  criteriaResults the List of critera results.
	 * @return                 true if all results are positive or if the criteriaResults are null or empty.
	 */
	private static boolean allPass(List<CriteriaResult> criteriaResults) {
		return ofNullable(criteriaResults).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.allMatch(CriteriaResult::value);
	}

	public static Result toNonApplicableResult(Rule rule) {
		return Result.create()
			.withRule(rule.getName())
			.withValue(NOT_APPLICABLE);
	}

	public static Result toValidationErrorResult(Rule rule, List<String> errorDescriptions) {
		return Result.create()
			.withDescriptions(errorDescriptions)
			.withRule(rule.getName())
			.withValue(VALIDATION_ERROR);
	}

	public static RuleEngineResponse toRuleEngineResponse(RuleEngine ruleEngine, List<Result> results) {
		return RuleEngineResponse.create()
			.withContext(String.valueOf(ruleEngine.getContext()))
			.withResults(results);
	}
}
