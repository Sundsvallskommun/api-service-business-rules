package se.sundsvall.businessrules;

import java.util.List;

import se.sundsvall.businessrules.api.model.Fact;
import se.sundsvall.businessrules.api.model.Result;
import se.sundsvall.businessrules.rule.Rule;

public class TestUtils {

	public static class TestRule implements Rule {

		@Override
		public Result validate(List<Fact> facts) {
			return null;
		}
	}
}
