package se.sundsvall.businessrules.rule.parkingpermit;

import static com.nimbusds.oauth2.sdk.util.CollectionUtils.isNotEmpty;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toMap;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toResult;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toValidationErrorResult;
import static se.sundsvall.businessrules.service.util.RuleEngineUtil.evaluateCriteria;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.Criteria;
import se.sundsvall.businessrules.rule.parkingpermit.criteria.BarCriteria;
import se.sundsvall.businessrules.rule.parkingpermit.criteria.FooCriteria;

@Component
// TODO: Remove this rule when the real rules are implemented. This rule is only here as a reference.
public class FooBarRule implements ParkingPermitRule {

	@Override
	public List<Class<? extends Criteria>> getCriteria() {
		return List.of(FooCriteria.class, BarCriteria.class);
	}

	@Override
	public boolean isApplicable(List<Fact> facts) {
		final var map = toMap(facts);
		return map.containsKey("foo") && map.containsKey("bar");
	}

	@Override
	public Result evaluate(List<Fact> facts) {

		final var validationErrors = validateInput(facts);
		if (isNotEmpty(validationErrors)) {
			return toValidationErrorResult(this, validationErrors);
		}

		// Evaluate all criteria for this rule.
		return toResult(this, evaluateCriteria(this, facts));
	}

	private List<String> validateInput(List<Fact> facts) {

		final var map = toMap(facts);
		final var fooValue = map.get("foo");
		final var barValue = map.get("bar");
		final var errorDescriptions = new ArrayList<String>();

		if ((fooValue == null) || !fooValue.matches("^true$|^false$")) {
			errorDescriptions.add("foo.value is not a boolean value (must be true or false only)");
		}
		if ((barValue == null) || !barValue.matches("^true$|^false$")) {
			errorDescriptions.add("bar.value is not a boolean value (must be true or false only)");
		}

		return errorDescriptions;
	}
}
