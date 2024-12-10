package se.sundsvall.businessrules.rule.impl.parkingpermit.criteria;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;

@ExtendWith(MockitoExtension.class)
class PoliceReportFormatCriteriaTest {

	@InjectMocks
	private PoliceReportFormatCriteria criteria;

	@Test
	void evaluateSuccess() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey()).withValue("5000-KXXXXXXX-2X"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isTrue();
		assertThat(result.description()).isEqualTo("polisrapportens diarienummer har korrekt format");
	}

	@Test
	void evaluateFailureDueToPoliceReportBadFormat() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey()).withValue("KXXXXXXX"));

		// Act
		final var result = criteria.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("polisrapportens diarienummer har felaktigt format");
	}

	@Test
	void evaluateFailureDueToPoliceReportMissing() {

		// Act
		final var municipalityId = "2281";
		final var result = criteria.evaluate(municipalityId, emptyList());

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.criteria()).isEqualTo(criteria);
		assertThat(result.value()).isFalse();
		assertThat(result.description()).isEqualTo("diarienummer för polisrapport saknas");
	}
}
