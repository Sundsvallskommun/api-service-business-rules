package se.sundsvall.businessrules.rule;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;

public interface Rule {

	/**
	 * Get rule name. This is by default the rule class name formatted as UPPER_UNDERSCORE.
	 *
	 * @return the rule name.
	 */
	default String getName() {
		return UPPER_CAMEL.to(UPPER_UNDERSCORE, this.getClass().getSimpleName());
	}

	/**
	 * Whether this rule should be applicable or not.
	 * If the rule is not applicable, it will not be evaluated by the rule engine.
	 *
	 * @return true if the rule is applicable, false otherwise.
	 */
	default boolean isApplicable(List<Fact> facts) {
		return true;
	}

	/**
	 * The rule evaluation method.
	 * This is where all conditions and criterias are evaluated.
	 *
	 * @param  municipalityId the municipalityId
	 * @param  facts          the facts to evaluate
	 * @return                the evaluation result.
	 */
	Result evaluate(String municipalityId, List<Fact> facts);

	/**
	 * The criteria classes that will be evaluated by this rule.
	 *
	 * @return all criteria that will be evaluated by this rule.
	 */
	List<Class<? extends Criteria>> getCriteria();
}
