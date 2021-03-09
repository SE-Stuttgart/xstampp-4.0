package de.xstampp.common.errors;

public final class ErrorsProj {
	private ErrorsProj() {
		throw new IllegalAccessError("Utility class");
	}

	/* PROJ-1xxx: General editing errors */

	public static final ErrorCondition ALREADY_EXISTS =
			new ErrorCondition("PROJ-1000", 417, "Conflict when saving: This entity already exists.");

	public static final ErrorCondition DOES_NOT_EXIST =
			new ErrorCondition("PROJ-1001", 417, "This entity does not exist.");

	/* PROJ-2xxx: Incorrect parameters */

	public static final ErrorCondition NEED_SORT_ARG =
			new ErrorCondition("PROJ-2001", 400, "At least one ordering is required");

	public static final ErrorCondition WRONG_SORT_ARG =
			new ErrorCondition("PROJ-2002", 400, "Invalid ordering supplied");

}
