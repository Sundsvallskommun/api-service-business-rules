package se.sundsvall.businessrules.service.engine.impl;

import static se.sundsvall.businessrules.service.Constants.MUNICIPALITY_ID_SUNDSVALL;

import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.businessrules.rule.impl.parkingpermit.ParkingPermitRule;

@Component
public class SundsvallParkingPermitRuleEngine extends ParkingPermitRuleEngine {

	public SundsvallParkingPermitRuleEngine(List<ParkingPermitRule> rules) {
		super(rules);
	}

	@Override
	public String getMunicipalityId() {
		return MUNICIPALITY_ID_SUNDSVALL;
	}
}
