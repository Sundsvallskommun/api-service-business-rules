package se.sundsvall.businessrules.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.TestUtils.TestCriteria;

import static org.assertj.core.api.Assertions.assertThat;

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
