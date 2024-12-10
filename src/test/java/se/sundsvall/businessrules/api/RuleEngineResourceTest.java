package se.sundsvall.businessrules.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.businessrules.Application;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.service.RuleEngineService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class RuleEngineResourceTest {

	private static final String PATH = "/2281/engine";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private RuleEngineService ruleEngineServiceMock;

	@LocalServerPort
	private int port;

	@Test
	void runEngine() {

		// Arrange
		final var ruleEngineRequest = ruleEngineRequest();

		when(ruleEngineServiceMock.run(any(), eq(ruleEngineRequest))).thenReturn(RuleEngineResponse.create()
			.withContext(PARKING_PERMIT.toString())
			.withResults(List.of(Result.create())));

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(ruleEngineRequest)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(RuleEngineResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(ruleEngineServiceMock).run("2281", ruleEngineRequest);
	}

	private static RuleEngineRequest ruleEngineRequest() {
		return RuleEngineRequest.create()
			.withContext(PARKING_PERMIT.toString())
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));
	}
}
