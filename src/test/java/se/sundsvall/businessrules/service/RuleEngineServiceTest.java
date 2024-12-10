package se.sundsvall.businessrules.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.RuleEngineResponse;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.service.engine.RuleEngine;

@ExtendWith(MockitoExtension.class)
class RuleEngineServiceTest {

	@Mock
	private RuleEngine ruleEngineMock;

	@InjectMocks
	private RuleEngineService ruleEngineExecutor;

	@BeforeEach
	void setup() {
		setField(ruleEngineExecutor, "ruleEngines", List.of(ruleEngineMock));
	}

	@Test
	void ruleEngineExistsAndIsExecuted() {

		// Arrange
		final var municipalityId = "2281";
		final var request = RuleEngineRequest.create()
			.withContext(Context.PARKING_PERMIT.toString())
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));

		when(ruleEngineMock.getContext()).thenReturn(Context.PARKING_PERMIT);
		when(ruleEngineMock.run(any(), any(RuleEngineRequest.class))).thenReturn(RuleEngineResponse.create());

		// Act
		final var result = ruleEngineExecutor.run(municipalityId, request);

		// Assert
		assertThat(result).isNotNull();
		verify(ruleEngineMock).getContext();
		verify(ruleEngineMock).run(municipalityId, request);
	}

	@Test
	void ruleEngineNotFound() {

		// Arrange
		final var municipalityId = "2281";
		final var context = Context.PARKING_PERMIT;
		final var request = RuleEngineRequest.create()
			.withContext(context.toString())
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));

		when(ruleEngineMock.getContext()).thenReturn(null);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> ruleEngineExecutor.run(municipalityId, request));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getDetail()).isEqualTo("No engine for context: 'PARKING_PERMIT' was found!");

		verify(ruleEngineMock).getContext();
		verify(ruleEngineMock, never()).run(municipalityId, request);
	}
}
