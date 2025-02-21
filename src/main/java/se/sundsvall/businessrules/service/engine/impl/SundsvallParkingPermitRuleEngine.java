package se.sundsvall.businessrules.service.engine.impl;

import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.service.Constants.MUNICIPALITY_ID_SUNDSVALL;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toNonApplicableResult;
import static se.sundsvall.businessrules.service.mapper.RuleEngineMapper.toRuleEngineResponse;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.rule.impl.parkingpermit.ParkingPermitRule;
import se.sundsvall.businessrules.service.engine.RuleEngine;

@Component
public class SundsvallParkingPermitRuleEngine implements RuleEngine {

	private final List<ParkingPermitRule> rules;

	public SundsvallParkingPermitRuleEngine(List<ParkingPermitRule> rules) {
		this.rules = rules;
	}

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
