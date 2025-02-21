package se.sundsvall.businessrules.service.engine.impl;

import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toNonApplicableResult;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toRuleEngineResponse;

import java.util.List;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.rule.Rule;
import se.sundsvall.businessrules.service.engine.RuleEngine;

public abstract class ParkingPermitRuleEngine implements RuleEngine {

	private final List<? extends Rule> rules;

	protected ParkingPermitRuleEngine(List<? extends Rule> rules) {
		this.rules = rules;
	}

	public Context getContext() {
		return PARKING_PERMIT;
	}

	public RuleEngineResponse run(String municipalityId, RuleEngineRequest request) {
		final var results = rules.stream()
			.map(rule -> {
				if (rule.isApplicable(request.getFacts())) {
					return rule.evaluate(municipalityId, request.getFacts());
				}
				return toNonApplicableResult(rule);
			})
			.toList();

		return toRuleEngineResponse(this, results);
	}
}
