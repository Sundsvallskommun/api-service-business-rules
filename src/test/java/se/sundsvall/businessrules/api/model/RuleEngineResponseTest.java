package se.sundsvall.businessrules.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.api.model.enums.Context;

class RuleEngineResponseTest {

	@Test
	void testBean() {
		assertThat(RuleEngineResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var context = Context.PARKING_PERMIT.toString();
		final var results = List.of(Result.create());

		final var bean = RuleEngineResponse.create()
			.withContext(context)
			.withResults(results);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getContext()).isEqualTo(context);
		assertThat(bean.getResults()).isEqualTo(results);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RuleEngineResponse.create()).hasAllNullFieldsOrProperties();
		assertThat(new RuleEngineResponse()).hasAllNullFieldsOrProperties();
	}
}
