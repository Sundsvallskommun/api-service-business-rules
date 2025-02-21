package se.sundsvall.businessrules.service.engine;

import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;

public interface RuleEngine {

	/**
	 * Returns the municipality id for which this rule engine is applicable for.
	 *
	 * @return the municipalityId
	 */
	String getMunicipalityId();

	/**
	 * Returns the current rule engine context.
	 *
	 * @return
	 */
	Context getContext();

	/**
	 * Run the rule engine.
	 *
	 * @param  municipalityId the municipalityId.
	 * @param  request        the rule enginge request.
	 * @return
	 */
	RuleEngineResponse run(String municipalityId, RuleEngineRequest request);
}
