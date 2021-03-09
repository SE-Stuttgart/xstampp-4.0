package de.xstampp.service.project.service.dao.test.project;

import java.util.UUID;

/**
 * Constants for tests with the project entity ({@link de.xstampp.service.project.data.entity.Project}).
 */
public abstract class ProjectEntityTestConstants {
	
	public static final UUID TEST_PROJECT_ID_1 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46101");
	public static final UUID TEST_PROJECT_ID_2 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46102");
	static final UUID TEST_PROJECT_ID_3 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46103");
	static final UUID TEST_PROJECT_ID_4 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46104");
	static final UUID TEST_PROJECT_ID_5 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46105");
	static final UUID TEST_PROJECT_ID_6 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46106");
	static final UUID TEST_PROJECT_ID_7 = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46107");

	public static final UUID TEST_NON_EXISTENT_PROJECT_ID = UUID.fromString("152835ca-589d-5cbb-a92c-010c4ad46125");
	
	public static final String TEST_DESCRIPTION = "Test Description";
	
	static final String TEST_PROJECT_NAME_1 = "JUnit Test Project 1";
	static final String TEST_PROJECT_NAME_2 = "JUnit Test Project 2";
	static final String TEST_PROJECT_NAME_3 = "JUnit Test Project 3";
	static final String TEST_PROJECT_NAME_4 = "JUnit Test Project 4";
	static final String TEST_PROJECT_NAME_5 = "JUnit Test Project 5";
	static final String TEST_PROJECT_NAME_6 = "JUnit Test Project 6";
	static final String TEST_PROJECT_NAME_7 = "JUnit Test Project 7";
	
	static final String TEST_REFERENCE_NUMBER = "Test RefNr";

	/**
	 * Private constructor to prevent from creating instances of this class.
	 */
	private ProjectEntityTestConstants() {
		super();
	}

}
