package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.PASSPORT_PHOTO_ATTACHMENT;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class PassportPhotoCriteriaTest {

	@InjectMocks
	private PassportPhotoCriteria criteria;

	@Test
	void evaluateSuccess() {

		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(PASSPORT_PHOTO_ATTACHMENT.getKey()).withValue("exists"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("passfoto har skickats in");
	}

	@Test
	void evaluateFailure() {
		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(PASSPORT_PHOTO_ATTACHMENT.getKey()).withValue("missing"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("passfoto har skickats in");
	}
}
