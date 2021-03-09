package de.xstampp.common.auth;

public final class TokenConstants {
	private TokenConstants() {
		throw new IllegalAccessError("Utility class");
	}

	public static final String CLAIM_UID = "uid";
	public static final String CLAIM_DISPLAY_NAME = "displayName";
	public static final String CLAIM_TOKEN_TYPE = "type";
	public static final String CLAIM_PROJECT_ID = "projectId";
	public static final String CLAIM_PROJECT_ROLE = "role";
	public static final String CLAIM_IS_SYSTEM_ADMIN = "isSystemAdmin";

	public static final String VALUE_CLAIM_TYPE_LONGLIVED = "long";
	public static final String VALUE_CLAIM_TYPE_SHORTLIVED = "short";
	
	// 7 days
	public final static long TIME_LONGLIVED_TOKEN_SECONDS = 60 * 60 * 24 * 7L;
	// 10 min
	public final static long TIME_SHORTLIVED_TOKEN_SECONDS = 60 * 10L;
}
