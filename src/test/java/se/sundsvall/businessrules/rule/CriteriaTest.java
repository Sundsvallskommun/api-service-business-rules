package se.sundsvall.businessrules.rule;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.TestUtils.TestCriteria;

@ExtendWith(MockitoExtension.class)
class CriteriaTest {

	@InjectMocks
	private TestCriteria criteria;

	@Test
	void defaultMethodImplementations() {

		// Assert
		assertThat(criteria.getName()).isEqualTo("TEST_CRITERIA");
	}
}
