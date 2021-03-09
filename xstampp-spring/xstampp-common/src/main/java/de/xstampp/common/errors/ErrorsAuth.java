package de.xstampp.common.errors;

public final class ErrorsAuth {
	private ErrorsAuth() {
		throw new IllegalAccessError("Utility class");
	}

	/* AUTH-1xxx: Login-related errors */

	public static final ErrorCondition LOGIN_FAILED =
			new ErrorCondition("AUTH-1000", 403, "Login failed. Username and/or password invalid");

	public static final ErrorCondition PASSWORDS_NOMATCH =
			new ErrorCondition("AUTH-1001", 400, "The supplied passwords do not match");

	public static final ErrorCondition PASSWORD_INCORRECT =
			new ErrorCondition("AUTH-1002", 403, "The supplied password is incorrect");

	/* AUTH-2xxx: Incorrect parameters */

	public static final ErrorCondition NEED_LONG_TOKEN =
			new ErrorCondition("AUTH-2000", 400, "You need to pass a long-lived token to request a short-lived (project) token");

	public static final ErrorCondition PRIVATE_GROUP_INVALID =
			new ErrorCondition("AUTH-2001", 400, "You cannot perform this action on a private group");

	public static final ErrorCondition CANNOT_MOVE_PROJECT =
			new ErrorCondition("AUTH-2002", 400, "You cannot move this project to another group");

	public static final ErrorCondition GROUPS_BLOCKING_USER_DELETION =
			new ErrorCondition("AUTH-2003", 409, "Deletion of your account is prohibited by your membership in certain groups");

	public static final ErrorCondition USER_DOESNT_EXIST =
			new ErrorCondition("AUTH-2004", 404, "This user doesn't exist");

	/* AUTH-9xxx: Internal errors */

	public static final ErrorCondition FAILED_PROJ_DEL =
			new ErrorCondition("AUTH-9000", 500, "Failed deleting project.");

	public static final ErrorCondition FAILED_PROJ_ADD =
			new ErrorCondition("AUTH-9001", 500, "Failed creating project.");

	public static final ErrorCondition FAILED_USER_ADD =
			new ErrorCondition("AUTH-9002", 500, "Failed creating user.");


}
