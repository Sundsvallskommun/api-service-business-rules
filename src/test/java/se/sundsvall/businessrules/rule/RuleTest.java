package se.sundsvall.businessrules.rule;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.TestUtils.TestRule;

@ExtendWith(MockitoExtension.class)
class RuleTest {

	@InjectMocks
	private TestRule rule;

	@Test
	void defaultMethodImplementations() {

		// Assert
		assertThat(rule.getName()).isEqualTo("TEST_RULE");
		assertThat(rule.isApplicable(null)).isTrue();
	}
}
