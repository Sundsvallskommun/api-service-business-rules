package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ATTACHMENT;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
public class SignatureCriteriaTest {

	@InjectMocks
	private SignatureCriteria criteria;

	@Test
	void evaluateSuccessWithSignature() {

		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(SIGNATURE_ATTACHMENT.getKey()).withValue("exists"),
			Fact.create().withKey(SIGNATURE_ABILITY.getKey()).withValue("true"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("signatur har skickats in");
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"missing", "exists"
	})
	void evaluateSuccessWithoutSignature(String attachmentValue) {
		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(SIGNATURE_ATTACHMENT.getKey()).withValue(attachmentValue),
			Fact.create().withKey(SIGNATURE_ABILITY.getKey()).withValue("false"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("signatur behövs ej");
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"missing", "", "abc"
	})
	void evaluateFailure(String attachmentValue) {
		final var municipalityId = "1234";
		final var facts = List.of(Fact.create().withKey(SIGNATURE_ATTACHMENT.getKey()).withValue(attachmentValue),
			Fact.create().withKey(SIGNATURE_ABILITY.getKey()).withValue("true"));

		final var result = criteria.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("signatur saknas");
	}
}
