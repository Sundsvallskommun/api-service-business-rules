package se.sundsvall.businessrules;

import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toResult;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.evaluateCriteria;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;
import se.sundsvall.businessrules.rule.Rule;
import se.sundsvall.businessrules.service.engine.RuleEngine;

public class TestUtils {

	public static class TestRuleEngine implements RuleEngine {

		@Override
		public Context getContext() {
			return PARKING_PERMIT;
		}

		@Override
		public RuleEngineResponse run(RuleEngineRequest request) {
			return null;
		}
	}

	public static class TestRule implements Rule {

		@Override
		public Result evaluate(List<Fact> facts) {
			return toResult(this, evaluateCriteria(this, facts));
		}

		@Override
		public List<Class<? extends Criteria>> getCriteria() {
			return List.of(TestCriteria.class);
		}
	}

	public static class TestCriteria implements Criteria {

		@Override
		public CriteriaResult evaluate(List<Fact> facts) {
			return new CriteriaResult(true, "OK");
		}
	}
}
