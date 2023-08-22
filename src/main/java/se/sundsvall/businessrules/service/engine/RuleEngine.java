package se.sundsvall.businessrules.service.engine;

import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;

public interface RuleEngine {

	Context getContext();

	RuleEngineResponse run(RuleEngineRequest request);
}
