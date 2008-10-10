// $Id$
package org.uccreator.repository;

import org.uccreator.model.ReposAccess;

/**
 * @author Kariem Hussein
 */
public class RepositoryAuthenticationException extends RepositoryConnectionException {

	/**
	 * @param message
	 *            the message
	 * @param access
	 *            the repository
	 * @param cause
	 *            the cause of the exception
	 */
	public RepositoryAuthenticationException(String message, ReposAccess access,
			Throwable cause) {
		super(message, access, cause);
	}

	/**
	 * @param message
	 *            the message
	 * @param access
	 *            the repository
	 */
	public RepositoryAuthenticationException(String message, ReposAccess access) {
		this(message, access, null);
	}

}
