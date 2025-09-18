package apptest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.MEDICAL_CONFIRMATION_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.PASSPORT_PHOTO_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ABILITY;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.SIGNATURE_ATTACHMENT;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID;
import static se.sundsvall.businessrules.rule.impl.parkingpermit.enums.ParkingPermitFactKeyEnum.TYPE;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@WireMockAppTestSuite(files = "classpath:/ParkingPermit/", classes = Application.class)
class ParkingPermitIT extends AbstractAppTest {

	private static final String BASE_PATH_SUNDSVALL = "/2281/engine";
	private static final String BASE_PATH_ANGE = "/2260/engine";
	private static final String REQUEST = "request.json";
	private static final String RESPONSE = "response.json";

	@Test
	void test01_noApplicableRulesApplied() {

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_passNewDriverParkingPermit() {

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_failNewDriverParkingPermit() { // Already has an active parkingpermit

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_passNewPassengerParkingPermit() {

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_failNewPassengerParkingPermit() { // Already has an active parkingpermit

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_passRenewalOfDriverParkingPermit() { // Has an active parking permit about to expire

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_failRenewalOfDriverParkingPermit() { // Has a active parking permit not about to expire

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_passRenewalOfPassengerParkingPermit() { // Has two parking permits, one exipired and one about to expire

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_failRenewalOfPassengerParkingPermit() { // Has two parking permits, one expired and one still active

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_passLostParkingPermit() {

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_failLostParkingPermit() { // Reoccurring reports of lost permit exists for applicant

		setupCall()
			.withServicePath(BASE_PATH_SUNDSVALL)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@ParameterizedTest
	@MethodSource("automatedTypesAndCapability")
	void test12_passParkingPermitAutomated(String type, String capability) {
		List<Fact> facts = new ArrayList<>(List.of(
			Fact.create().withKey(TYPE.getKey()).withValue(type),
			Fact.create().withKey(MEDICAL_CONFIRMATION_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(SIGNATURE_ABILITY.getKey()).withValue("true"),
			Fact.create().withKey(SIGNATURE_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(PASSPORT_PHOTO_ATTACHMENT.getKey()).withValue("true"),
			Fact.create().withKey(STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).withValue("1cc22a36-6960-4dbf-a40d-580385752f4e")));
		if (capability != null) {
			facts.add(Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue(capability));
		}

		var request = RuleEngineRequest.create()
			.withContext("PARKING_PERMIT")
			.withFacts(facts);

		setupCall()
			.withServicePath(BASE_PATH_ANGE)
			.withHttpMethod(POST)
			.withRequest(new Gson().toJson(request))
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@ParameterizedTest
	@MethodSource("automatedTypesAndCapability")
	void test13_failParkingPermitAutomated(String type, String capability) {
		List<Fact> facts = new ArrayList<>(List.of(
			Fact.create().withKey(TYPE.getKey()).withValue(type)));
		if (capability != null) {
			facts.add(Fact.create().withKey(APPLICATION_APPLICANT_CAPACITY.getKey()).withValue(capability));
		}

		var request = RuleEngineRequest.create()
			.withContext("PARKING_PERMIT")
			.withFacts(facts);

		setupCall()
			.withServicePath(BASE_PATH_ANGE)
			.withHttpMethod(POST)
			.withRequest(new Gson().toJson(request))
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test14_noApplicableRulesAppliedAutomated() {
		setupCall()
			.withServicePath(BASE_PATH_ANGE)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	private static Stream<Arguments> automatedTypesAndCapability() {
		return Stream.of(
			Arguments.of("PARKING_PERMIT", "DRIVER"),
			Arguments.of("PARKING_PERMIT", "PASSENGER"),
			Arguments.of("PARKING_PERMIT_RENEWAL", "DRIVER"),
			Arguments.of("PARKING_PERMIT_RENEWAL", "PASSENGER"),
			Arguments.of("LOST_PARKING_PERMIT", null));
	}
}
