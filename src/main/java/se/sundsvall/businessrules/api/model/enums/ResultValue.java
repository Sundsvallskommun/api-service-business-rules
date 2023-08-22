package se.sundsvall.businessrules.api.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ResultValue model", enumAsRef = true)
public enum ResultValue {
	PASS,
	FAIL,
	VALIDATION_ERROR,
	NOT_APPLICABLE
}
