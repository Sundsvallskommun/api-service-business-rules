package se.sundsvall.businessrules.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;

import org.junit.jupiter.api.Test;

class ResultValueTest {

	@Test
	void testEnumValues() {
		assertThat(ResultValue.values()).containsExactly(PASS, FAIL, VALIDATION_ERROR, NOT_APPLICABLE);
	}
}
