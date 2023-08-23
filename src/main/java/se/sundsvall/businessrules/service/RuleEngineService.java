package se.sundsvall.businessrules.service;

import static java.lang.String.format;
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
		return findRuleEngineByContext(Context.valueOf(request.getContext())).run(request);
	}

	private RuleEngine findRuleEngineByContext(Context context) {
		return ruleEngines.stream()
			.filter(engine -> Objects.equals(engine.getContext(), context))
			.findFirst().orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_NO_ENGINE_FOUND, context)));
	}
}
