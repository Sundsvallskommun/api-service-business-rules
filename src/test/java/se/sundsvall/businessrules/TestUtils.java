package se.sundsvall.businessrules;

import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.service.Constants.MUNICIPALITY_ID_SUNDSVALL;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toResult;

import java.util.List;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.CriteriaResult;
import se.sundsvall.businessrules.rule.Rule;
import se.sundsvall.businessrules.service.engine.RuleEngine;

public final class TestUtils {

	private TestUtils() {}

	public static class TestRuleEngine implements RuleEngine {

		@Override
		public String getMunicipalityId() {
			return MUNICIPALITY_ID_SUNDSVALL;
		}

		@Override
		public Context getContext() {
			return PARKING_PERMIT;
		}

		@Override
		public RuleEngineResponse run(String municipalityId, RuleEngineRequest request) {
			return null;
		}
	}

	public static class TestRule implements Rule {

		@Override
		public Result evaluate(String municipalityId, List<Fact> facts) {
			return toResult(this, CriteriaEvaluator.evaluateCriteria(this, municipalityId, facts));
		}

		@Override
		public List<Class<? extends Criteria>> getCriteria() {
			return List.of(TestCriteria.class);
		}
	}

	public static class TestCriteria implements Criteria {

		@Override
		public CriteriaResult evaluate(String municipalityId, List<Fact> facts) {
			return new CriteriaResult(true, "OK", this);
		}
	}
}
