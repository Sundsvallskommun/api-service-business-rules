package se.sundsvall.businessrules.api.validation.impl;

import static java.util.Objects.isNull;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;
import se.sundsvall.businessrules.api.validation.ValueOfEnum;

public class ValueOfEnumConstraintValidator implements ConstraintValidator<ValueOfEnum, String> {

	private boolean nullable;
	private String message;
	private List<String> acceptedValues;

	@Override
	public void initialize(ValueOfEnum annotation) {
		this.message = annotation.message();
		this.nullable = annotation.nullable();
		this.acceptedValues = Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name).toList();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (isNull(value) && this.nullable) {
			return true;
		}
		final var isValid = acceptedValues.contains(value);
		if (!isValid) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message + " [accepted values are: " + String.join(",", acceptedValues) + "]").addConstraintViolation();
		}

		return isValid;
	}
}
