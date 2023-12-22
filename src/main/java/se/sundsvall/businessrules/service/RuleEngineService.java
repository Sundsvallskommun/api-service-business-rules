package se.sundsvall.businessrules.service;

import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.service.engine.RuleEngine;

@Service
public class RuleEngineService {

	static final String ERROR_MESSAGE_NO_ENGINE_FOUND = "No engine for context: '%s' was found!";

	private final List<RuleEngine> ruleEngines;

	public RuleEngineService(List<RuleEngine> ruleEngines) {
		this.ruleEngines = ruleEngines;
	}

	public RuleEngineResponse run(RuleEngineRequest request) {
		return findRuleEngineByContext(Context.valueOf(request.getContext())).run(request);
	}

	private RuleEngine findRuleEngineByContext(Context context) {
		return ruleEngines.stream()
			.filter(engine -> Objects.equals(engine.getContext(), context))
			.findFirst().orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_NO_ENGINE_FOUND.formatted(context)));
	}
}
