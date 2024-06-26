package se.sundsvall.businessrules.rule;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.function.Failable;
import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;

@Component
public class CriteriaEvaluator {

	private final List<Criteria> criteria;

	public CriteriaEvaluator(List<Criteria> criteria) {
		this.criteria = criteria;
	}

	/**
	 * Evaluate all criteria for a specific rule.
	 * This method evaluates component criteria. I.e. criteria that is spring beans.
	 *
	 * @param  rule           the rule that holds the criteria to evaluate.
	 * @param  municipalityId the municipalityId.
	 * @param  facts          the facts provided to the rule.
	 * @return                a List of CriteraResult:s
	 */
	public List<CriteriaResult> evaluateCriteriaComponent(Rule rule, String municipalityId, List<Fact> facts) {
		return Optional.ofNullable(criteria).orElse(emptyList()).stream()
			.filter(c -> isPartOfRule(rule, c)) // Only criteria that is defined in the rule should be evaluated.
			.map(c -> c.evaluate(municipalityId, facts)) // Evaluate criteria.
			.toList();
	}

	/**
	 * Evaluate all criteria for a specific rule.
	 * This method evaluates non-component criteria. i.e. criteria that is POJO:s.
	 *
	 * @param  rule           the rule that holds the criteria to evaluate.
	 * @param  municipalityId the municipalityId.
	 * @param  facts          the facts provided to the rule.
	 * @return                a List of CriteraResult:s
	 */
	public static List<CriteriaResult> evaluateCriteria(Rule rule, String municipalityId, List<Fact> facts) {
		return Failable.stream(Optional.ofNullable(rule.getCriteria()).orElse(emptyList()).stream())
			.map(criteriaClass -> criteriaClass.getConstructor().newInstance()) // Instantiate criteria.
			.map(c -> c.evaluate(municipalityId, facts)) // Evaluate criteria.
			.stream()
			.toList();
	}

	/**
	 * Check if a criteria is a part of a Rule. I.e. defined in rule.getCriteria().
	 *
	 * @param  rule     the rule.
	 * @param  criteria the criteria to check.
	 * @return          true if the criteria is defined in the rule.getCriteria(), false otherwise.
	 */
	private boolean isPartOfRule(Rule rule, Criteria criteria) {
		return Optional.ofNullable(rule.getCriteria()).orElse(emptyList())
			.contains(criteria.getClass());
	}
}
