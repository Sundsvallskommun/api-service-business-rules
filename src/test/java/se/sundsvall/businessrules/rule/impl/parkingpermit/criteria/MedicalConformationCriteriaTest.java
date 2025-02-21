package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.MEDICAL_CONFIRMATION_ATTACHMENT;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
public class MedicalConformationCriteriaTest {

	@InjectMocks
	private MedicalConformationCriteria criteria;

	@Test
	void evaluateSuccess() {

		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(MEDICAL_CONFIRMATION_ATTACHMENT.getKey()).withValue("exists"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("läkarintyg har skickats in");
	}

	@Test
	void evaluateFailure() {
		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(MEDICAL_CONFIRMATION_ATTACHMENT.getKey()).withValue("missing"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("läkarintyg saknas");
	}
}
