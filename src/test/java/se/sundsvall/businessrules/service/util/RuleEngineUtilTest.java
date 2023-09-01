package se.sundsvall.businessrules.service.util;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import se.sundsvall.businessrules.api.model.Fact;

class RuleEngineUtilTest {

	@ParameterizedTest
	@MethodSource("isValidUUIDArguments")
	void isValidUUID(String uuid, boolean expectedResult) {
		assertThat(RuleEngineUtil.isValidUUID(uuid)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> isValidUUIDArguments() {
		return Stream.of(
			Arguments.of(randomUUID().toString(), true),
			Arguments.of("not-valid-uuid", false),
			Arguments.of("", false),
			Arguments.of(null, false));
	}

	@ParameterizedTest
	@MethodSource("hasValidUUIDArguments")
	void hasValidUUIDValue(Fact fact, boolean expectedResult) {
		assertThat(RuleEngineUtil.hasValidUUIDValue(fact)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> hasValidUUIDArguments() {
		return Stream.of(
			Arguments.of(Fact.create().withValue(randomUUID().toString()), true),
			Arguments.of(Fact.create().withValue("not-valid-uuid"), false),
			Arguments.of(Fact.create().withValue(""), false),
			Arguments.of(Fact.create(), false));
	}

	@ParameterizedTest
	@MethodSource("hasValidBooleanValueArguments")
	void hasValidBooleanValue(Fact fact, boolean expectedResult) {
		assertThat(RuleEngineUtil.hasValidBooleanValue(fact)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> hasValidBooleanValueArguments() {
		return Stream.of(
			Arguments.of(Fact.create().withValue("true"), true),
			Arguments.of(Fact.create().withValue("TRUE"), true),
			Arguments.of(Fact.create().withValue("false"), true),
			Arguments.of(Fact.create().withValue("FALSE"), true),
			Arguments.of(Fact.create().withValue(""), false),
			Arguments.of(Fact.create().withValue("not-true"), false),
			Arguments.of(Fact.create().withValue("yes"), false),
			Arguments.of(Fact.create(), false));
	}

	@ParameterizedTest
	@MethodSource("hasValidNumericValueArguments")
	void hasValidNumericValue(Fact fact, boolean expectedResult) {
		assertThat(RuleEngineUtil.hasValidNumericValue(fact)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> hasValidNumericValueArguments() {
		return Stream.of(
			Arguments.of(Fact.create().withValue("10"), true),
			Arguments.of(Fact.create().withValue("100000000000000000"), true),
			Arguments.of(Fact.create().withValue("0"), true),
			Arguments.of(Fact.create().withValue("-10"), true),
			Arguments.of(Fact.create().withValue(""), false),
			Arguments.of(Fact.create().withValue("five"), false),
			Arguments.of(Fact.create().withValue("1,56"), false),
			Arguments.of(Fact.create(), false));
	}

	@ParameterizedTest
	@MethodSource("hasValueArguments")
	void hasValidValue(Fact fact, boolean expectedResult) {
		assertThat(RuleEngineUtil.hasValue(fact)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> hasValueArguments() {
		return Stream.of(
			Arguments.of(Fact.create().withValue("foo"), true),
			Arguments.of(Fact.create().withValue(""), true),
			Arguments.of(Fact.create().withValue("10"), true),
			Arguments.of(Fact.create().withValue("-10"), true),
			Arguments.of(Fact.create().withValue("true"), true),
			Arguments.of(Fact.create().withValue(randomUUID().toString()), true),
			Arguments.of(Fact.create(), false));
	}

	@ParameterizedTest
	@MethodSource("matchesArguments")
	void matches(Fact fact, String regExp, boolean expectedResult) {
		assertThat(RuleEngineUtil.matches(fact, regExp)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> matchesArguments() {
		return Stream.of(
			Arguments.of(Fact.create().withValue("43"), "^[\\d]*$", true),
			Arguments.of(Fact.create().withValue("abc"), "^[\\d]*$", false),
			Arguments.of(Fact.create().withValue("hello-world"), "^(hello-world)$", true),
			Arguments.of(Fact.create().withValue("hello-man"), "^(hello-world)$", false),
			Arguments.of(Fact.create(), "", false),
			Arguments.of(Fact.create(), null, false),
			Arguments.of(null, "^[\\d]*$", false),
			Arguments.of(null, null, false));
	}

	@Test
	void toInt() {
		assertThat(RuleEngineUtil.toInt(Fact.create().withValue("42"))).isEqualTo(42);
		assertThat(RuleEngineUtil.toInt(Fact.create().withValue("-42"))).isEqualTo(-42);
	}

	@Test
	void toBoolean() {
		assertThat(RuleEngineUtil.toBoolean(Fact.create().withValue("true"))).isTrue();
		assertThat(RuleEngineUtil.toBoolean(Fact.create().withValue("false"))).isFalse();
	}
}
