package se.sundsvall.businessrules.rule.parkingpermit.criteria;

import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toMap;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.CriteriaResult;

//TODO: Remove this criteria when the real criteria are implemented. This criteria is only here as a reference.
public class FooCriteria implements Criteria {

	@Override
	public CriteriaResult evaluate(List<Fact> facts) {
		final var factMap = toMap(facts);

		if ("true".equals(factMap.get("foo"))) {
			return new CriteriaResult(true, "OK: foo is true");
		}

		return new CriteriaResult(false, "FAIL: foo is not true");
	}
}
