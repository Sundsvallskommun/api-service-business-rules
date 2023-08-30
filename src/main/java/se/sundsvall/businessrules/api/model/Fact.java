package se.sundsvall.businessrules.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static java.util.Objects.nonNull;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Fact model")
public class Fact {

	@Schema(description = "The attribute key", example = "attribute-x", requiredMode = REQUIRED)
	@NotBlank
	private String key;

	@Schema(description = "The attribute value", example = "Some value")
	@NotNull
	private String value;

	public static Fact create() {
		return new Fact();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Fact withKey(String key) {
		this.key = key;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Fact withValue(String value) {
		this.value = value;
		return this;
	}

	public boolean hasBooleanValue() {
		return nonNull(this.value) && this.value.matches("^(?i)(true|false)$");
	}

	public boolean hasNumericValue() {
		return nonNull(this.value) && this.value.matches("^-?\\d+$");
	}

	public boolean hasValue() {
		return nonNull(this.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final Fact other)) { return false; }
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Fact [key=").append(key).append(", value=").append(value).append("]");
		return builder.toString();
	}
}
