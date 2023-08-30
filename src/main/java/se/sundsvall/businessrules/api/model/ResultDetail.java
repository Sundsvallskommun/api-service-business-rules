package se.sundsvall.businessrules.api.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RuleResultDetail model")
public class ResultDetail {

	@Schema(description = "The result detail origin", example = "THE_SPECIAL_RULE_CRITERIA")
	private String origin;

	@Schema(description = "The result detail evaluation value", example = "false")
	private boolean evaluationValue;

	@Schema(description = "The result detail description", example = "Failed because of reasons")
	private String description;

	public static ResultDetail create() {
		return new ResultDetail();
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public ResultDetail withOrigin(String origin) {
		this.origin = origin;
		return this;
	}

	public boolean getEvaluationValue() {
		return evaluationValue;
	}

	public void setEvaluationValue(boolean evaluationValue) {
		this.evaluationValue = evaluationValue;
	}

	public ResultDetail withEvaluationValue(boolean evaluationValue) {
		this.evaluationValue = evaluationValue;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ResultDetail withDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, evaluationValue, origin);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final ResultDetail other)) { return false; }
		return Objects.equals(description, other.description) && (evaluationValue == other.evaluationValue) && Objects.equals(origin, other.origin);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ResultDetail [origin=").append(origin).append(", evaluationValue=").append(evaluationValue).append(", description=").append(description).append("]");
		return builder.toString();
	}
}
