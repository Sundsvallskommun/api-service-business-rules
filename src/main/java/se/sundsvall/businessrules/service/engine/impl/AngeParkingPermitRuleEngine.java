package se.sundsvall.businessrules.service.engine.impl;

import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.service.Constants.MUNICIPALITY_ID_ANGE;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toNonApplicableResult;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toRuleEngineResponse;

import java.util.List;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.rule.Rule;
import se.sundsvall.businessrules.rule.impl.parkingpermit.ParkingPermitRuleAutomated;
import se.sundsvall.businessrules.service.engine.RuleEngine;

public class AngeParkingPermitRuleEngine implements RuleEngine {

	private final List<? extends Rule> rules;

	public AngeParkingPermitRuleEngine(List<ParkingPermitRuleAutomated> automatedRules) {
		this.rules = automatedRules;
	}

	@Override
	public String getMunicipalityId() {
		return MUNICIPALITY_ID_ANGE;
	}

	@Override
	public Context getContext() {
		return PARKING_PERMIT;
	}

	@Override
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
