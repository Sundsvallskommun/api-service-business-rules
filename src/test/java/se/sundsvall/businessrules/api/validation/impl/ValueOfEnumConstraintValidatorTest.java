package se.sundsvall.businessrules.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import se.sundsvall.businessrules.api.validation.ValueOfEnum;

@ExtendWith(MockitoExtension.class)
class ValueOfEnumConstraintValidatorTest {

	@Mock
	private ConstraintViolationBuilder constraintViolationBuilderMock;

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@Mock
	private ValueOfEnum annotationMock;

	@InjectMocks
	private ValueOfEnumConstraintValidator validator;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ParameterizedTest
	@MethodSource("valueOfEnumProvider")
	void validContactChannel(Class enumType, String enumValue, boolean exprectedResult) {

		when(annotationMock.enumClass()).thenReturn(enumType);

		validator.initialize(annotationMock);

		// Arrange
		lenient().when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var isValid = validator.isValid(enumValue, constraintValidatorContextMock);

		// Assert
		assertThat(isValid).isEqualTo(exprectedResult);
	}

	/**
	 * For this test, we have chosen the enum class: ElementType, but any enum will work.
	 */
	private static Stream<Arguments> valueOfEnumProvider() {
		return Stream.of(

			// Valid enum value strings.
			Arguments.of(ElementType.class, "ANNOTATION_TYPE", true),
			Arguments.of(ElementType.class, "FIELD", true),
			Arguments.of(ElementType.class, "METHOD", true),
			Arguments.of(ElementType.class, "PARAMETER", true),
			Arguments.of(ElementType.class, "CONSTRUCTOR", true),
			Arguments.of(ElementType.class, "LOCAL_VARIABLE", true),

			// Invalid enum value strings.
			Arguments.of(ElementType.class, "ANNTATION", false),
			Arguments.of(ElementType.class, "field", false),
			Arguments.of(ElementType.class, "MeThOd", false),
			Arguments.of(ElementType.class, "Param", false),
			Arguments.of(ElementType.class, "C", false),
			Arguments.of(ElementType.class, "LOCALVARIABLE", false));
	}
}
