package se.sundsvall.businessrules.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.api.validation.ValueOfEnum;

@Schema(description = "RuleEngineRequest model")
public class RuleEngineRequest {

	@Schema(description = "The rule engine context", example = "PARKING_PERMIT", requiredMode = REQUIRED)
	@ValueOfEnum(enumClass = Context.class, message = "Invalid context value!")
	private String context;

	@Schema(description = "Alias for the destination", requiredMode = REQUIRED)
	@NotEmpty
	private List<@Valid Fact> facts;

	public static RuleEngineRequest create() {
		return new RuleEngineRequest();
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public RuleEngineRequest withContext(String context) {
		this.context = context;
		return this;
	}

	public List<Fact> getFacts() {
		return facts;
	}

	public void setFacts(List<Fact> facts) {
		this.facts = facts;
	}

	public RuleEngineRequest withFacts(List<Fact> facts) {
		this.facts = facts;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(context, facts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final RuleEngineRequest other)) { return false; }
		return Objects.equals(context, other.context) && Objects.equals(facts, other.facts);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RuleEngineRequest [context=").append(context).append(", facts=").append(facts).append("]");
		return builder.toString();
	}
}
