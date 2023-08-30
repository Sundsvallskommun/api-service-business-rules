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

class ResultDetailTest {

	@Test
	void testBean() {
		assertThat(ResultDetail.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var description = "description";
		final var evaluationValue = true;
		final var origin = "origin";

		final var bean = ResultDetail.create()
			.withDescription(description)
			.withEvaluationValue(evaluationValue)
			.withOrigin(origin);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getEvaluationValue()).isEqualTo(evaluationValue);
		assertThat(bean.getOrigin()).isEqualTo(origin);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ResultDetail.create()).hasAllNullFieldsOrPropertiesExcept("evaluationValue");
		assertThat(new ResultDetail()).hasAllNullFieldsOrPropertiesExcept("evaluationValue");
	}
}
