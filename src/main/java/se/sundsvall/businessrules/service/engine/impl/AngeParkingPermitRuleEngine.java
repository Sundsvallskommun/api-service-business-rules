package se.sundsvall.businessrules.service.engine.impl;

import static se.sundsvall.businessrules.service.Constants.MUNICIPALITY_ID_ANGE;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.rule.impl.parkingpermit.ParkingPermitRuleAutomated;

@Component
public class AngeParkingPermitRuleEngine extends ParkingPermitRuleEngine {

	public AngeParkingPermitRuleEngine(List<ParkingPermitRuleAutomated> automatedRules) {
		super(automatedRules);
	}

	@Override
	public String getMunicipalityId() {
		return MUNICIPALITY_ID_ANGE;
	}

}
