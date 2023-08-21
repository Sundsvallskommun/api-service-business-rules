package se.sundsvall.businessrules.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;

import org.junit.jupiter.api.Test;

class ResultValueTest {

	@Test
	void testEnumValues() {
		assertThat(ResultValue.values()).containsExactly(PASS, FAIL, NOT_APPLICABLE);
	}
}
