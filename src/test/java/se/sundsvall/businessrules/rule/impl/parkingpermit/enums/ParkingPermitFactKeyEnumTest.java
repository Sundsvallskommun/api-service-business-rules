package se.sundsvall.businessrules.rule.impl.parkingpermit.enums;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ParkingPermitFactKeyEnumTest {

	@Test
	void validParameterKeyValues() {
		assertThat(Stream.of(ParkingPermitFactKeyEnum.values())
			.map(ParkingPermitFactKeyEnum::getKey)
			.toList()).containsExactlyInAnyOrder(
				"type",
				"application.applicant.capacity",
				"application.lostPermit.policeReportNumber",
				"application.renewal.changedCircumstances",
				"disability.canBeAloneWhileParking",
				"disability.duration",
				"disability.walkingAbility",
				"disability.walkingDistance.max",
				"stakeholders.applicant.personid");
	}

	@Test
	void enumKeyValues() {
		assertThat(ParkingPermitFactKeyEnum.TYPE.getKey()).isEqualTo("type");
		assertThat(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getKey()).isEqualTo("application.applicant.capacity");
		assertThat(ParkingPermitFactKeyEnum.APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getKey()).isEqualTo("application.lostPermit.policeReportNumber");
		assertThat(ParkingPermitFactKeyEnum.APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES.getKey()).isEqualTo("application.renewal.changedCircumstances");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getKey()).isEqualTo("disability.canBeAloneWhileParking");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getKey()).isEqualTo("disability.duration");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getKey()).isEqualTo("disability.walkingAbility");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getKey()).isEqualTo("disability.walkingDistance.max");
		assertThat(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getKey()).isEqualTo("stakeholders.applicant.personid");
	}

	@Test
	void enumDescriptionValues() {
		assertThat(ParkingPermitFactKeyEnum.TYPE.getDescription()).isEqualTo("typ av ärende");
		assertThat(ParkingPermitFactKeyEnum.APPLICATION_APPLICANT_CAPACITY.getDescription()).isEqualTo("ändamål att parkeringstillstånd söks (förare eller passagerare)");
		assertThat(ParkingPermitFactKeyEnum.APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER.getDescription()).isEqualTo("diarienummer från polisanmälan");
		assertThat(ParkingPermitFactKeyEnum.APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES.getDescription()).isEqualTo("eventuell förändring av förutsättningarna sedan föregående beslut kring p-tillstånd");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_CAN_BE_ALONE_WHILE_PARKING.getDescription()).isEqualTo("upplysning ifall den sökande kan lämnas ensam eller ej under tiden bilen parkeras");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_DURATION.getDescription()).isEqualTo("funktionsnedsättningens varaktighet");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_WALKING_ABILITY.getDescription()).isEqualTo("information om sökande har kapacitet att gå eller är rullstolsburen");
		assertThat(ParkingPermitFactKeyEnum.DISABILITY_WALKING_DISTANCE_MAX.getDescription()).isEqualTo("uppgift om maximal gångsträcka för den sökande");
		assertThat(ParkingPermitFactKeyEnum.STAKEHOLDERS_APPLICANT_PERSON_ID.getDescription()).isEqualTo("personid för sökande person");
	}
}
