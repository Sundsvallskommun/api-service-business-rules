package se.sundsvall.businessrules.rule.impl.parkingpermit;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.FAIL;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.PASS;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.VALIDATION_ERROR;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.PARTY_ID_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.STATUS_PARAMETER;
import static se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient.TYPE_PARAMETER;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.MEDICAL_CONFIRMATION_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.PASSPORT_PHOTO_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.ResultDetail;
import se.sundsvall.businessrules.integration.partyassets.PartyAssetsClient;
import se.sundsvall.businessrules.rule.CriteriaEvaluator;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.MedicalConformationCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.NoActiveParkingPermitCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.PassportPhotoCriteria;
import se.sundsvall.businessrules.rule.impl.parkingpermit.criteria.SignatureCriteria;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class ParkingPermitAutomatedRuleTest {

	@MockitoBean
	private PartyAssetsClient partyAssetsClientMock;

	@MockitoSpyBean
	private CriteriaEvaluator criteriaEvaluatorSpy;

	@Autowired
	private ParkingPermitRuleAutomated rule;

	@Test
	void getCriteria() {
		assertThat(rule.getCriteria()).containsExactly(
			NoActiveParkingPermitCriteria.class,
			MedicalConformationCriteria.class,
			PassportPhotoCriteria.class,
			SignatureCriteria.class);
	}

	@ParameterizedTest
	@MethodSource("applicableTypesAndCapabilities")
	void isApplicableIsTrue(String type, String capability) {

		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue(type),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue(capability));

		final var result = rule.isApplicable(facts);

		assertThat(result).isTrue();
	}

	public static Stream<Arguments> applicableTypesAndCapabilities() {
		return Stream.of(
			Arguments.of("PARKING_PERMIT", "DRIVER"),
			Arguments.of("PARKING_PERMIT", "PASSENGER"),
			Arguments.of("PARKING_PERMIT_RENEWAL", "DRIVER"),
			Arguments.of("PARKING_PERMIT_RENEWAL", "PASSENGER"),
			Arguments.of("LOST_PARKING_PERMIT", null));
	}

	@Test
	void isApplicableIsFalse() {

		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue("INVALID_TYPE"));// Not valid for this rule.

		final var result = rule.isApplicable(facts);

		assertThat(result).isFalse();
	}

	@ParameterizedTest
	@MethodSource("applicableTypesAndCapabilities")
	void evaluateSuccess(String type, String capability) {
		final var municipalityId = "1234";
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue(type),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue(capability),
			Fact.create().withKey(MEDICAL_CONFIRMATION_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(PASSPORT_PHOTO_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(SIGNATURE_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(SIGNATURE_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		final var result = rule.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PARKING_PERMIT_AUTOMATED_RULE");
		assertThat(result.getValue()).isEqualTo(PASS);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("MEDICAL_CONFORMATION_CRITERIA")
				.withDescription("läkarintyg har skickats in"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("NO_ACTIVE_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga aktiva parkeringstillstånd"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("PASSPORT_PHOTO_CRITERIA")
				.withDescription("passfoto har skickats in"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("SIGNATURE_CRITERIA")
				.withDescription("signatur har skickats in")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, municipalityId, facts);
		verify(partyAssetsClientMock).getAssets(municipalityId, Map.of(PARTY_ID_PARAMETER, partyId, STATUS_PARAMETER, "ACTIVE", TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@ParameterizedTest
	@MethodSource("applicableTypesAndCapabilities")
	void evaluateFailureDueToFailedCriteria(String type, String capability) {
		final var municipalityId = "1234";
		final var partyId = randomUUID().toString();
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue(type),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue(capability),
			Fact.create().withKey(MEDICAL_CONFIRMATION_ATTACHMENT.getKey()).withValue("false"), // Will make it fail
			Fact.create().withKey(PASSPORT_PHOTO_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(SIGNATURE_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(SIGNATURE_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue(partyId));

		final var result = rule.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PARKING_PERMIT_AUTOMATED_RULE");
		assertThat(result.getValue()).isEqualTo(FAIL);
		assertThat(result.getDetails()).isEqualTo(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withOrigin("MEDICAL_CONFORMATION_CRITERIA")
				.withDescription("läkarintyg saknas"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("NO_ACTIVE_PARKING_PERMIT_CRITERIA")
				.withDescription("den sökande har inga aktiva parkeringstillstånd"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("PASSPORT_PHOTO_CRITERIA")
				.withDescription("passfoto har skickats in"),
			ResultDetail.create()
				.withEvaluationValue(true)
				.withOrigin("SIGNATURE_CRITERIA")
				.withDescription("signatur har skickats in")));

		verify(criteriaEvaluatorSpy).evaluateCriteriaComponent(rule, municipalityId, facts);
		verify(partyAssetsClientMock).getAssets(municipalityId, Map.of(PARTY_ID_PARAMETER, partyId, STATUS_PARAMETER, "ACTIVE", TYPE_PARAMETER, "PARKINGPERMIT"));
	}

	@ParameterizedTest
	@MethodSource("applicableTypesAndCapabilities")
	void evaluateFailureDueToAllMandatoryParametersMissing(String type, String capability) {
		final var municipalityId = "1234";
		final var facts = List.of(
			Fact.create().withKey(TYPE.getKey()).withValue(type),
			Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue(capability));

		final var result = rule.evaluate(municipalityId, facts);

		assertThat(result).isNotNull();
		assertThat(result.getRule()).isEqualTo("PARKING_PERMIT_AUTOMATED_RULE");
		assertThat(result.getValue()).isEqualTo(VALIDATION_ERROR);
		assertThat(result.getDetails()).containsExactlyInAnyOrderElementsOf(List.of(
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'attachment.medicalConfirmation' (läkarintyg för sökande)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'attachment.signature' (signatur för sökande)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'application.applicant.signingAbility' (sökande har anget att denne signera själv)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'attachment.passportPhoto' (passfoto för den sökande)"),
			ResultDetail.create()
				.withEvaluationValue(false)
				.withDescription("Saknar giltigt värde för: 'stakeholders.applicant.personid' (personid för sökande person)")));

		verifyNoInteractions(criteriaEvaluatorSpy);
		verifyNoInteractions(partyAssetsClientMock);
	}
}
