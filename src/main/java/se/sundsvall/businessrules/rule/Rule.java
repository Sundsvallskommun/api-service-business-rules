package se.sundsvall.businessrules.rule;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;

public interface Rule {

	default String getName() {
		return UPPER_CAMEL.to(UPPER_UNDERSCORE, this.getClass().getSimpleName());
	}

	default boolean isApplicable(List<Fact> facts) {
		return true;
	}

	Result validate(List<Fact> facts);
}
