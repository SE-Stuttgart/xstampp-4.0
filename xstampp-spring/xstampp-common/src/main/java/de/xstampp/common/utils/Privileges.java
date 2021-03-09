package de.xstampp.common.utils;

/**
 * Holds the group-level privileges. Notably system adminship is not a privilege
 * that a user has for a group, instead it's an inherent property of a user.
 */
public enum Privileges {
	GUEST, DEVELOPER, ANALYST, GROUP_ADMIN
}
