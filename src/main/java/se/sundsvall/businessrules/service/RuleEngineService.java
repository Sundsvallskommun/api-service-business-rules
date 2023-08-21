package se.sundsvall.businessrules.service;

import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.service.engine.RuleEngine;

@Service
public class RuleEngineService {

	static final String ERROR_MESSAGE_NO_ENGINE_FOUND = "No engine for context: '%s' was found!";

	@Autowired
	private List<RuleEngine> ruleEngines;

	public RuleEngineResponse run(RuleEngineRequest request) {
		final var ruleEngine = getRuleEngineByContext(Context.valueOf(request.getContext()));
		return ruleEngine.run(request);
	}

	private RuleEngine getRuleEngineByContext(Context context) {
		return ruleEngines.stream()
			.filter(engine -> Objects.equals(engine.getRuleContext(), context))
			.findFirst().orElseThrow(() -> Problem.valueOf(NOT_FOUND, String.format(ERROR_MESSAGE_NO_ENGINE_FOUND, context)));
	}
}
