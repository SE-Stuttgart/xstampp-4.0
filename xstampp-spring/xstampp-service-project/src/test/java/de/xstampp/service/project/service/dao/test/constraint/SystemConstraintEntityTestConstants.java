package de.xstampp.service.project.service.dao.test.constraint;

/**
 * Constants for tests with the
 * {@link de.xstampp.service.project.data.entity.SystemConstraint} entity.
 */
abstract class SystemConstraintEntityTestConstants {
	
	static final int TEST_SYSTEM_CONSTRAINT_ID = 1;
	
	static final int TEST_NON_EXISTENT_SYSTEM_CONSTRAINT_ID = 1005;
	
	static final String TEST_SYSTEM_CONSTRAINT_NAME = "JUnit Test System Constraint 1";

	/**
	 * Private constructor to prevent from creating instances of this class.
	 */
	private SystemConstraintEntityTestConstants() {
		super();
	}

}
