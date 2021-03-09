package de.xstampp.common.errors;

public class ErrorsPerm {
	private ErrorsPerm() {
		throw new IllegalAccessError("Utility class");
	}

	/* PERM-1xxx: Permission errors for actions on users and groups */

	public static final ErrorCondition PERMISSION_DENIED =
			new ErrorCondition("PERM-1000", 403, "Permission denied.");

	public static final ErrorCondition NEED_SYSADM =
			new ErrorCondition("PERM-1001", 403, "You do not have permission to administrative actions on the system");

	public static final ErrorCondition NEED_GROUPADM =
			new ErrorCondition("PERM-1002", 403, "You do not have permission to perform administrative actions on this group");

	public static final ErrorCondition NEED_GROUP_MEMBER =
			new ErrorCondition("PERM-1003", 403, "You do not have permission to view this group");

	public static final ErrorCondition NEED_GROUP_ADMPROJ =
			new ErrorCondition("PERM-1004", 403, "You do not have permission to create or delete projects in this group");

	/* PERM-2xxx: Permission errors for projects */

	public static final ErrorCondition NEED_PROJ_VIEW =
			new ErrorCondition("PERM-2000", 403, "You do not have permission to view this project");

	public static final ErrorCondition NEED_PROJ_EDIT =
			new ErrorCondition("PERM-2001", 403, "You do not have permission to edit this project");

	public static final ErrorCondition NEED_PROJ_GENERIC =
			new ErrorCondition("PERM-2999", 403, "You do not have permission to perform this action on this project");

	/* PERM-3xxx: When checking a token fails */
	
	public static final ErrorCondition TOKEN_MISSING =
			new ErrorCondition("PERM-3000", 401, "Authorization token missing");

	public static final ErrorCondition TOKEN_INVALID =
			new ErrorCondition("PERM-3001", 401, "Authorization token malformed or invalid");
	
	public static final ErrorCondition TOKEN_MISMATCHING =
			new ErrorCondition("PERM-3002", 401, "ProjectId mismatches the project token rights");

}
