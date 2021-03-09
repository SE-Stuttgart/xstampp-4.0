package de.xstampp.service.project.exceptions;

import de.xstampp.common.errors.ErrorsProj;

/**
 * @deprecated to be replaced with error conditions, as seen in
 *             {@link ErrorsProj}
 */
@Deprecated(forRemoval = true)
public class ProjectException extends RuntimeException{

	private static final long serialVersionUID = 6287275557856098863L;
	
	private final String userVisibleMessage;

	public ProjectException(String userVisibleMessage) {
		super(userVisibleMessage);
		this.userVisibleMessage = userVisibleMessage;
	}

	public String getErrorMessage() {
		return userVisibleMessage;
	}
}
