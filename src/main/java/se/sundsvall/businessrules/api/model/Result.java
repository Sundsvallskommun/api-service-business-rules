package se.sundsvall.businessrules.api.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.businessrules.api.model.enums.ResultValue;

@Schema(description = "Result model")
public class Result {

	@Schema(description = "The result value", example = "PASS")
	private ResultValue value;

	@Schema(description = "The name of the rule that generated the result", example = "The almighty business rule")
	private String rule;

	@Schema(description = "Rule result description", example = "Failed because of reasons")
	private String description;

	public static Result create() {
		return new Result();
	}

	public ResultValue getValue() {
		return value;
	}

	public void setValue(ResultValue value) {
		this.value = value;
	}

	public Result withValue(ResultValue value) {
		this.value = value;
		return this;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Result withRule(String rule) {
		this.rule = rule;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Result withDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, rule, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final Result other)) { return false; }
		return Objects.equals(description, other.description) && Objects.equals(rule, other.rule) && (value == other.value);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Result [value=").append(value).append(", rule=").append(rule).append(", description=").append(description).append("]");
		return builder.toString();
	}
}
