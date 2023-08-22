package se.sundsvall.businessrules.rule;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;

public interface Criteria {

	/**
	 * Get criteria name. This is by default the criteria class name formatted as UPPER_UNDERSCORE.
	 *
	 * @return the criteria name.
	 */
	default String getName() {
		return UPPER_CAMEL.to(UPPER_UNDERSCORE, this.getClass().getSimpleName());
	}

	/**
	 * The criteria evaluation method.
	 * This is where all conditions are evaluated.
	 *
	 * @return the evaluation result.
	 */
	CriteriaResult evaluate(List<Fact> facts);
}
