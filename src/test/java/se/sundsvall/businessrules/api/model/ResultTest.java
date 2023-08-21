package se.sundsvall.businessrules.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.api.model.enums.ResultValue;

class ResultTest {

	@Test
	void testBean() {
		assertThat(Result.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var description = "description";
		final var rule = "rule";
		final var value = ResultValue.PASS;

		final var bean = Result.create()
			.withDescription(description)
			.withRule(rule)
			.withValue(value);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getRule()).isEqualTo(rule);
		assertThat(bean.getValue()).isEqualTo(value);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Result.create()).hasAllNullFieldsOrPropertiesExcept("value");
		assertThat(new Result()).hasAllNullFieldsOrPropertiesExcept("value");
	}
}
