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

class RuleEngineRequestTest {

	@Test
	void testBean() {
		assertThat(RuleEngineRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var context = Context.PARKING_PERMIT.toString();
		final var facts = List.of(Fact.create());

		final var bean = RuleEngineRequest.create()
			.withContext(context)
			.withFacts(facts);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getContext()).isEqualTo(context);
		assertThat(bean.getFacts()).isEqualTo(facts);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RuleEngineRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new RuleEngineRequest()).hasAllNullFieldsOrProperties();
	}
}
