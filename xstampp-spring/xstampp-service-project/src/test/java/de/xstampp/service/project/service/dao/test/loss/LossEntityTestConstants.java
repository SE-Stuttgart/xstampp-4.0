package de.xstampp.service.project.service.dao.test.loss;

/**
 * Constants for tests with the loss entity
 * ({@link de.xstampp.service.project.data.entity.Loss}).
 */
abstract class LossEntityTestConstants {

	static final int TEST_LOSS_ID_1 = 1;
	static final int TEST_LOSS_ID_2 = 2;
	static final int TEST_LOSS_ID_3 = 3;
	
	static final int TEST_NON_EXISTENT_LOSS_ID = 1002;
	
	static final String TEST_LOSS_NAME_1 = "JUnit Test Loss 1";
	static final String TEST_LOSS_NAME_2 = "JUnit Test Loss 2";
	static final String TEST_LOSS_NAME_3 = "JUnit Test Loss 3";

	/**
	 * Private constructor to prevent from creating instances of this class.
	 */
	private LossEntityTestConstants() {
		super();
	}

}
