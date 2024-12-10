package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;

import generated.se.sundsvall.partyassets.Asset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.ResultDetail;
import se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.PoliceReportFormatCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.RecurringLossesCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class LostParkingPermitRuleTest {

	private static final String PARTY_ID_PARAMETER = "partyId";
	private static final String TYPE_PARAMETER = "type";
	private static final String STATUS_PARAMETER = "status";
	private static final String STATUS_REASON_PARAMETER = "statusReason";

	@MockitoBean
	private PartyAssetsClient partyAssetsClientMock;

	@SpyBean
	private CriteriaEvaluator criteriaEvaluatorSpy;

	@Autowired
	private LostParkingPermitRule rule;

	@Test
	void getCriteria() {
		assertThat(rule.getCriteria()).containsExactly(
			PoliceReportFormatCriteria.class,
			RecurringLossesCriteria.class);
	}

	@Test
	void isApplicableIsTrue() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("LOST_PARKING_PERMIT"));

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	void isApplicableIsFalse() {

		// Arrange
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("INVALID_TYPE"));// Not valid for this rule.

		// Act
		final var result = rule.isApplicable(facts);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	void evaluateSuccess() {

		// Arrange
		final var municipalityId = "2281";
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("LOST_PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey()).withValue("5000-KABCDABC-2A"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("LOST_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(PASS);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("POLICE_REPORT_FORMAT_CRITERIA")
				.withDescription("polisrapportens diarienummer har korrekt format"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("RECURRING_LOSSES_CRITERIA")
				.withDescription("det finns inga tidigare förlustanmälningar")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, municipalityId, facts);
		verify(partyAssetsClientMock).getAssets(municipalityId, Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, "BLOCKED",
			STATUS_REASON_PARAMETER, "LOST",
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureDueToFailedCriteria() {

		// Arrange
		final var municipalityId = "2281";
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("LOST_PARKING_PERMIT"),
			Fact.create().withKey(APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey()).withValue("5000-KXXXXXXX-2X"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		// This will make the evaluation fail (recurring losses)
		when(partyAssetsClientMock.getAssets(any(), any())).thenReturn(List.of(new Asset()));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("LOST_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("POLICE_REPORT_FORMAT_CRITERIA")
				.withDescription("polisrapportens diarienummer har korrekt format"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withOrigin("RECURRING_LOSSES_CRITERIA")
				.withDescription("det finns tidigare förlustanmälningar")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, municipalityId, facts);
		verify(partyAssetsClientMock).getAssets(municipalityId, Map.of(
			PARTY_ID_PARAMETER, partyId,
			STATUS_PARAMETER, "BLOCKED",
			STATUS_REASON_PARAMETER, "LOST",
			TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@Test
	void evaluateFailureDueToAllMandatoryParametersMissing() {

		// Arrange
		final var municipalityId = "2281";
		final var facts = List.of(
			Fact.create().withKey(ParkingPermitFactKeyEnum.TYPE.getKey()).withValue("LOST_PARKING_PERMIT"));

		// Act
		final var result = rule.evaluate(municipalityId, facts);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("LOST_PARKING_PERMIT_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'application.lostPermit.policeReportNumber' (diarienummer från polisanmälan)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}
}
