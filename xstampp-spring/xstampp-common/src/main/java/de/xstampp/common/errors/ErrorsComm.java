package de.xstampp.common.errors;

public class ErrorsComm {
	private ErrorsComm() {
		throw new IllegalAccessError("Utility class");
	}

	public static final ErrorCondition JSON_DECODING_FAILED =
			new ErrorCondition("COMM-1000", 400, "Could not decode json object")
			.withShowNextMessage();

	public static final ErrorCondition DB_ACCESS_FAILED =
			new ErrorCondition("COMM-2000", 500, "Unknown error accessing database");
}
