package de.xstampp.service.project.service.dao.iface;

import de.xstampp.common.errors.ErrorsProj;

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
		throw ErrorsProj.WRONG_SORT_ARG.exc();
	}
}
