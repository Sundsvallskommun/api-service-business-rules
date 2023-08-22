package se.sundsvall.businessrules.service.util;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.function.Failable;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.CriteriaResult;
import se.sundsvall.businessrules.rule.Rule;

public class RuleEngineUtil {

	private RuleEngineUtil() {}

	/**
	 * Evaluate all criteria for a specific rule.
	 *
	 * @param  rule  the rule that holds the criteria to evaluate.
	 * @param  facts the facts provided to the rule.
	 * @return       a List of CriteraResult:s
	 */
	public static List<CriteriaResult> evaluateCriteria(Rule rule, List<Fact> facts) {
		return Failable.stream(Optional.ofNullable(rule.getCriteria()).orElse(emptyList()).stream())
			.map(criteriaClass -> criteriaClass.getConstructor().newInstance()) // Instantiate criteria
			.map(criteria -> criteria.evaluate(facts)) // Evaluate criteria
			.stream()
			.toList();
	}
}
