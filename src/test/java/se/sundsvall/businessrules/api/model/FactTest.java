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

class FactTest {

	@Test
	void testBean() {
		assertThat(Fact.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var key = "key";
		final var value = "value";

		final var bean = Fact.create()
			.withKey(key)
			.withValue(value);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getKey()).isEqualTo(key);
		assertThat(bean.getValue()).isEqualTo(value);
	}

	@Test
	void testValueMethods() {

		// Arrange
		final var fact = Fact.create().withKey("key");

		// hasNumericValue()
		assertThat(fact.withValue("five").hasNumericValue()).isFalse();
		assertThat(fact.withValue(" ").hasNumericValue()).isFalse();
		assertThat(fact.withValue(null).hasNumericValue()).isFalse();
		assertThat(fact.withValue("3").hasNumericValue()).isTrue();
		assertThat(fact.withValue("-666").hasNumericValue()).isTrue();

		// hasBooleanValue()
		assertThat(fact.withValue("No").hasBooleanValue()).isFalse();
		assertThat(fact.withValue(" ").hasBooleanValue()).isFalse();
		assertThat(fact.withValue("true false").hasBooleanValue()).isFalse();
		assertThat(fact.withValue(null).hasBooleanValue()).isFalse();
		assertThat(fact.withValue("true").hasBooleanValue()).isTrue();
		assertThat(fact.withValue("false").hasBooleanValue()).isTrue();
		assertThat(fact.withValue("TRUE").hasBooleanValue()).isTrue();
		assertThat(fact.withValue("FALSE").hasBooleanValue()).isTrue();
		assertThat(fact.withValue("TrUe").hasBooleanValue()).isTrue();
		assertThat(fact.withValue("FaLsE").hasBooleanValue()).isTrue();

		// hasValue()
		assertThat(fact.withValue(null).hasValue()).isFalse();
		assertThat(fact.withValue(" ").hasValue()).isTrue();
		assertThat(fact.withValue("hello").hasValue()).isTrue();
		assertThat(fact.withValue("").hasValue()).isTrue();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Fact.create()).hasAllNullFieldsOrProperties();
		assertThat(new Fact()).hasAllNullFieldsOrProperties();
	}
}
