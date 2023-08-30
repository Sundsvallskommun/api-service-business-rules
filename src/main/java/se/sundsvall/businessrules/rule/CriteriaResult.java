package se.sundsvall.businessrules.rule;

/**
 * Models the criteria result.
 */
public record CriteriaResult(boolean value, String description, Criteria criteria) {}
