package de.xstampp.common.errors;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class GenericXSTAMPPException extends RuntimeException {
	private static final long serialVersionUID = 8341748677551708974L;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneOffset.UTC);

	private final ErrorCondition errCond;
	private final String formattedTimestamp;

	public GenericXSTAMPPException(ErrorCondition errCond) {
		super(errCond.getUserVisibleMessage());
		this.errCond = errCond;
		this.formattedTimestamp = genFormattedTimestamp();
	}

	public GenericXSTAMPPException(ErrorCondition errCond, Throwable cause) {
		super(errCond.getUserVisibleMessage(), cause);
		this.errCond = errCond;
		this.formattedTimestamp = genFormattedTimestamp();
	}

	public GenericXSTAMPPException(ErrorCondition errCond, String internalMessage) {
		super(internalMessage);
		this.errCond = errCond;
		this.formattedTimestamp = genFormattedTimestamp();
	}

	public GenericXSTAMPPException(ErrorCondition errCond, Throwable cause, String internalMessage) {
		super(internalMessage, cause);
		this.errCond = errCond;
		this.formattedTimestamp = genFormattedTimestamp();
	}

	private static String genFormattedTimestamp() {
		return FORMATTER.format(Instant.now());
	}

	public int getHttpStatusCode() {
		return errCond.getHttpStatusCode();
	}

	public String getFormattedTimestamp() {
		return formattedTimestamp;
	}

	/**
	 * Returns a reply object for API clients
	 *
	 * @return Reply object for API clients
	 */
	public ErrorReplyObject getReply() {
		String detailMessage = null;
		if (errCond.isShowNextMesage()) {
			Throwable cause = this.getCause();
			if (cause != null) {
				detailMessage = cause.getMessage();
			}
		}

		return new ErrorReplyObject(errCond, detailMessage, formattedTimestamp);
	}

}
