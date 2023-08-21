package se.sundsvall.businessrules.service.mapper;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import se.sundsvall.businessrules.api.model.Fact;

public class RuleEngineMapper {

	private RuleEngineMapper() {}

	public static Map<String, String> toMap(List<Fact> facts) {
		return Optional.ofNullable(facts).orElse(emptyList()).stream()
			.collect(Collectors.toMap(Fact::getKey, Fact::getValue));
	}
}
