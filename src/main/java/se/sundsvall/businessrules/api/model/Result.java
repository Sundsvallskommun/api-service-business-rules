package se.sundsvall.businessrules.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.businessrules.api.model.enums.ResultValue;

@Schema(description = "Result model")
public class Result {

	@Schema(description = "The result value", examples = "PASS")
	private ResultValue value;

	@Schema(description = "The name of the rule that generated the result", examples = "The almighty business rule")
	private String rule;

	@Schema(description = "Rule result details")
	private List<ResultDetail> details;

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

	public List<ResultDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ResultDetail> details) {
		this.details = details;
	}

	public Result withDetails(List<ResultDetail> details) {
		this.details = details;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(details, rule, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final Result other)) { return false; }
		return Objects.equals(details, other.details) && Objects.equals(rule, other.rule) && (value == other.value);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Result [value=").append(value).append(", rule=").append(rule).append(", details=").append(details).append("]");
		return builder.toString();
	}
}
