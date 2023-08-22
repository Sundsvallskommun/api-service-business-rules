package se.sundsvall.businessrules.rule;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;

public interface Criteria {

	default String getName() {
		return UPPER_CAMEL.to(UPPER_UNDERSCORE, this.getClass().getSimpleName());
	}

	CriteriaResult evaluate(List<Fact> facts);
}
