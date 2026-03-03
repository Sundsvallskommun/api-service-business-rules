package se.sundsvall.businessrules.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.service.RuleEngineService;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class RuleEngineResourceFailuresTest {

	private static final String PATH = "/2281/engine";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private RuleEngineService ruleEngineServiceMock;

	@Test
	void runEngineEmptyBody() {

		// Arrange
		final var ruleEngineRequest = RuleEngineRequest.create();

		// Act
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("context", "Invalid context value! [accepted values are: PARKING_PERMIT]"),
				tuple("context", "must not be null"),
				tuple("facts", "must not be empty"));

		verifyNoInteractions(ruleEngineServiceMock);
	}

	@Test
	void runEngineEmptyFacts() {

		// Arrange
		final var ruleEngineRequest = ruleEngineRequest().withFacts(emptyList());

		// Act
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("facts", "must not be empty"));

		verifyNoInteractions(ruleEngineServiceMock);
	}

	@Test
	void runEngineEmptyFactKey() {

		// Arrange
		final var ruleEngineRequest = ruleEngineRequest()
			.withFacts(List.of(Fact.create()
				.withKey(null)
				.withValue("value")));

		// Act
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("facts[0].key", "must not be blank"));

		verifyNoInteractions(ruleEngineServiceMock);
	}

	@Test
	void runEngineInvalidMunicipalityId() {

		// Arrange
		final var ruleEngineRequest = ruleEngineRequest()
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));

		// Act
		final var response = webTestClient.post()
			.uri("/666/engine") // Invalid municipalityId
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("run.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(ruleEngineServiceMock);
	}

	@Test
	void runEngineNullFactValue() {

		// Arrange
		final var ruleEngineRequest = ruleEngineRequest()
			.withFacts(List.of(Fact.create()
				.withKey("key")));

		// Act
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("facts[0].value", "must not be null"));

		verifyNoInteractions(ruleEngineServiceMock);
	}

	@Test
	void runEngineBadContext() {

		// Arrange
		final var ruleEngineRequest = ruleEngineRequest().withContext("Bad context");

		// Act
		final var response = webTestClient.post()
			.uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(
				tuple("context", "Invalid context value! [accepted values are: PARKING_PERMIT]"));

		verifyNoInteractions(ruleEngineServiceMock);
	}

	private static RuleEngineRequest ruleEngineRequest() {
		return RuleEngineRequest.create()
			.withContext(PARKING_PERMIT.toString())
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));
	}
}
