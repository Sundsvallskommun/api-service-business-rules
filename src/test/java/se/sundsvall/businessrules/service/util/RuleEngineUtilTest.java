package se.sundsvall.businessrules.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RuleEngineUtilTest {

	@ParameterizedTest
	@MethodSource("isValidUuidArguments")
	void isValidUuid(String uuid, boolean expectedResult) {
		assertThat(RuleEngineUtil.isValidUuid(uuid)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> isValidUuidArguments() {
		return Stream.of(
			Arguments.of(UUID.randomUUID().toString(), true),
			Arguments.of("not-valid-uuid", false),
			Arguments.of("", false),
			Arguments.of(null, false));
	}
}
