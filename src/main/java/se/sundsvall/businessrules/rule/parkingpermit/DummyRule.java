package se.sundsvall.businessrules.rule.parkingpermit;

import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toMap;

import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;

@Component
// TODO: Remove this rule when the real rules are implemented. This rule is only here as a placeholder.
public class DummyRule implements ParkingPermitRule {

	private static final String MANDATORY_ATTRIBUTE_X = "x";

	@Override
	public boolean isApplicable(List<Fact> facts) {
		return toMap(facts).containsKey(MANDATORY_ATTRIBUTE_X);
	}

	@Override
	public Result validate(List<Fact> facts) {

		final var factMap = toMap(facts);

		if ("y".equals(factMap.get(MANDATORY_ATTRIBUTE_X))) {

			// Passed.
			return Result.create()
				.withRule(getName())
				.withValue(PASS)
				.withDescription("OK");
		}

		// Failed.
		return Result.create()
			.withRule(getName())
			.withValue(FAIL)
			.withDescription("Missing valid value for x!");
	}
}
