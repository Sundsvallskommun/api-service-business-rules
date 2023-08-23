package se.sundsvall.businessrules.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import se.sundsvall.businessrules.api.validation.impl.ValueOfEnumConstraintValidator;

/**
 * The annotated String element must contain a valid enum value (from the provided enum type).
 */
@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumConstraintValidator.class)
@NotNull
public @interface ValueOfEnum {

	/**
	 * Returns the message.
	 *
	 * @return the message.
	 */
	String message() default "Invalid enum value";

	/**
	 * Controls whether the value can be null or not.
	 *
	 * If set to true, the validator will accept the value as valid when null.
	 * If set to false (default), the validator will reject the value as invalid when null.
	 *
	 * @return true if the value is accepted as nullable, false otherwise.
	 */
	boolean nullable() default false;

	@SuppressWarnings("rawtypes")
	Class<? extends Enum> enumClass();

	/**
	 * Returns the groups.
	 *
	 * @return the groups.
	 */
	Class<?>[] groups() default {};

	/**
	 * Returns the payload.
	 *
	 * @return the payload.
	 */
	Class<? extends Payload>[] payload() default {};
}
