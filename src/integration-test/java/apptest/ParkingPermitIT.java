package apptest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;

import se.sundsvall.businessrules.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/ParkingPermit/", classes = Application.class)
class ParkingPermitIT extends AbstractAppTest {

	private static final String BASE_PATH = "/engine";
	private static final String REQUEST = "request.json";
	private static final String RESPONSE = "response.json";

	@Test
	void test01_noApplicableRulesApplied() throws Exception {

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_passNewDriverParkingPermit() throws Exception {

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_failNewDriverParkingPermit() throws Exception { // Already has an active parkingpermit

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_passNewPassengerParkingPermit() throws Exception {

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_failNewPassengerParkingPermit() throws Exception { // Already has an active parkingpermit

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_passRenewalOfDriverParkingPermit() throws Exception { // Has an active parking permit about to expire

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_failRenewalOfDriverParkingPermit() throws Exception { // Has a active parking permit not about to expire

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_passRenewalOfPassengerParkingPermit() throws Exception { // Has two parking permits, one exipired and one about to expire

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_failRenewalOfPassengerParkingPermit() throws Exception { // Has two parking permits, one expired and one still active

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_passLostParkingPermit() throws Exception {

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_failLostParkingPermit() throws Exception { // Reoccurring reports of lost permit exists for applicant

		setupCall()
			.withServicePath(BASE_PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponse(RESPONSE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}
}
