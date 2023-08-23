package se.sundsvall.businessrules.api.model;

import java.util.List;
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
	private List<String> descriptions;

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

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public Result withDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descriptions, rule, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final Result other)) { return false; }
		return Objects.equals(descriptions, other.descriptions) && Objects.equals(rule, other.rule) && (value == other.value);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Result [value=").append(value).append(", rule=").append(rule).append(", descriptions=").append(descriptions).append("]");
		return builder.toString();
	}
}
