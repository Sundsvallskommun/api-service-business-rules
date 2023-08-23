package se.sundsvall.businessrules.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;

import org.junit.jupiter.api.Test;

class ContextTest {

	@Test
	void testEnumValues() {
		assertThat(Context.values()).containsExactly(PARKING_PERMIT);
	}
}
