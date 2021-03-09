package de.xstampp.service.project.service.dao.test.hazard;

/**
 * Constants for tests with the hazard entity
 * {@link de.xstampp.service.project.data.entity.Hazard}.
 */
public abstract class HazardEntityTestConstants {
	
	public static final int TEST_HAZARD_ID_1 = 1;
	static final int TEST_HAZARD_ID_2 = 2;
	static final int TEST_HAZARD_ID_3 = 3;
	
	static final int TEST_NON_EXISTENT_HAZARD_ID = 1009;
	
	public static final String TEST_HAZARD_NAME_1 = "JUnit Test Hazard 1";
	static final String TEST_HAZARD_NAME_2 = "JUnit Test Hazard 2";
	static final String TEST_HAZARD_NAME_3 = "JUnit Test Hazard 3";

	/**
	 * Private constructor to prevent from creating instances of this class.
	 */
	private HazardEntityTestConstants() {
		super();
	}

}
