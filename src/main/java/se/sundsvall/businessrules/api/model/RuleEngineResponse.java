package se.sundsvall.businessrules.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "RuleEngineResponse model")
public class RuleEngineResponse {

	@Schema(description = "The rule engine context", example = "PARKING_PERMIT")
	private String context;

	@Schema(description = "The rule engine results")
	private List<Result> results;

	public static RuleEngineResponse create() {
		return new RuleEngineResponse();
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public RuleEngineResponse withContext(String context) {
		this.context = context;
		return this;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public RuleEngineResponse withResults(List<Result> results) {
		this.results = results;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(context, results);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final RuleEngineResponse other)) { return false; }
		return (context == other.context) && Objects.equals(results, other.results);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RuleEngineResponse [context=").append(context).append(", results=").append(results).append("]");
		return builder.toString();
	}
}
