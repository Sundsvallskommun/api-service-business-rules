package se.sundsvall.businessrules.service.engine.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static se.sundsvall.businessrules.api.model.enums.Context.PARKING_PERMIT;
import static se.sundsvall.businessrules.api.model.enums.ResultValue.NOT_APPLICABLE;
import static se.sundsvall.businessrules.service.Constants.MUNICIPALITY_ID_ANGE;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.api.model.RuleEngineRequest;
import se.sundsvall.businessrules.api.model.enums.Context;
import se.sundsvall.businessrules.rule.impl.parkingpermit.ParkingPermitRuleAutomated;
import se.sundsvall.businessrules.service.engine.RuleEngine;

@ExtendWith(MockitoExtension.class)
class AngeParkingPermitRuleEngineTest {
	@Mock
	private ParkingPermitRuleAutomated rule1Mock;

	@Mock
	private ParkingPermitRuleAutomated rule2Mock;

	@InjectMocks
	private AngeParkingPermitRuleEngine parkingPermitRuleEngine;

	@BeforeEach
	void setup() {
		setField(parkingPermitRuleEngine, "rules", List.of(rule1Mock, rule2Mock));
	}

	@Test
	void ruleEngineImplementation() {

		assertThat(parkingPermitRuleEngine)
			.isInstanceOf(RuleEngine.class)
			.isExactlyInstanceOf(AngeParkingPermitRuleEngine.class);

		assertThat(parkingPermitRuleEngine.getContext()).isEqualTo(PARKING_PERMIT);
		assertThat(parkingPermitRuleEngine.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID_ANGE);
	}

	@Test
	void ruleEngineExecutesTheRules() {

		// Arrange
		final var municipalityId = MUNICIPALITY_ID_ANGE;
		final var request = RuleEngineRequest.create()
			.withContext(Context.PARKING_PERMIT.toString())
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));

		when(rule1Mock.isApplicable(any())).thenReturn(true);
		when(rule2Mock.isApplicable(any())).thenReturn(true);

		// Act
		final var result = parkingPermitRuleEngine.run(municipalityId, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getResults())
			.hasSize(2);

		verify(rule1Mock).isApplicable(request.getFacts());
		verify(rule2Mock).isApplicable(request.getFacts());
		verify(rule1Mock).evaluate(municipalityId, request.getFacts());
		verify(rule2Mock).evaluate(municipalityId, request.getFacts());
	}

	@Test
	void ruleEngineWhenRulesAreNotApplicable() {

		// Arrange
		final var municipalityId = MUNICIPALITY_ID_ANGE;
		final var request = RuleEngineRequest.create()
			.withContext(Context.PARKING_PERMIT.toString())
			.withFacts(List.of(Fact.create()
				.withKey("key")
				.withValue("value")));

		when(rule1Mock.getName()).thenReturn("RULE-1");
		when(rule2Mock.getName()).thenReturn("RULE-2");
		when(rule1Mock.isApplicable(any())).thenReturn(false);
		when(rule2Mock.isApplicable(any())).thenReturn(false);

		// Act
		final var result = parkingPermitRuleEngine.run(municipalityId, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getResults())
			.hasSize(2)
			.extracting(Result::getRule, Result::getValue)
			.containsExactly(
				tuple("RULE-1", NOT_APPLICABLE),
				tuple("RULE-2", NOT_APPLICABLE));

		verify(rule1Mock).isApplicable(request.getFacts());
		verify(rule2Mock).isApplicable(request.getFacts());
		verify(rule1Mock, never()).evaluate(municipalityId, request.getFacts());
		verify(rule2Mock, never()).evaluate(municipalityId, request.getFacts());
	}
}
