package se.sundsvall.businessrules.rule.parkingpermit.enums;

import static java.util.Objects.requireNonNull;

public enum ParkingPermitFactKeyEnum {

	TYPE(
		"type",
		"typ av ärende"),

	APPLICATION_APPLICANT_CAPACITY(
		"application.applicant.capacity",
		"ändamål att parkeringstillstånd söks (förare eller passagerare)"),

	APPLICATION_LOST_PERMIT_POLICE_REPORT_NUMBER(
		"application.lostPermit.policeReportNumber",
		"diarienummer från polisanmälan"),

	APPLICATION_RENEWAL_CHANGED_CIRCUMSTANCES(
		"application.renewal.changedCircumstances",
		"eventuell förändring av förutsättningarna sedan föregående beslut kring p-tillstånd"),

	DISABILITY_CAN_BE_ALONE_WHILE_PARKING(
		"disability.canBeAloneWhileParking",
		"upplysning ifall den sökande kan lämnas ensam eller ej under tiden bilen parkeras"),

	DISABILITY_DURATION(
		"disability.duration",
		"funktionsnedsättningens varaktighet"),

	DISABILITY_WALKING_ABILITY(
		"disability.walkingAbility",
		"information om sökande har kapacitet att gå eller är rullstolsburen"),

	DISABILITY_WALKING_DISTANCE_MAX(
		"disability.walkingDistance.max",
		"uppgift om maximal gångsträcka för den sökande"),

	STAKEHOLDERS_APPLICANT_PERSON_ID(
		"stakeholders.applicant.personid",
		"personid för sökande person");

	private final String description;
	private final String key;

	ParkingPermitFactKeyEnum(String key, String description) {
		requireNonNull(key, "key must be defined!");
		requireNonNull(description, "description must be specified!");

		this.key = key;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getKey() {
		return key;
	}
}
