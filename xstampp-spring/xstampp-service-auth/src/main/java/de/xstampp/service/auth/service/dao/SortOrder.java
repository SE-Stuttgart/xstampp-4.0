package de.xstampp.service.auth.service.dao;

/**
 * Used to specify sort order of the results.
 */
public enum SortOrder {
	ASC, DESC;

	public static SortOrder valueOfIgnoreCase(String s) {
		if ("ASC".equalsIgnoreCase(s)) {
			return SortOrder.ASC;
		} else if ("DESC".equalsIgnoreCase(s)) {
			return SortOrder.DESC;
		}
		throw new IllegalArgumentException("No such SortOrder " + s);
	}
}
