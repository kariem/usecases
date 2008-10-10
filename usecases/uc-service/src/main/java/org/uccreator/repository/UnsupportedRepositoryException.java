// $Id$
package org.uccreator.repository;

import org.uccreator.model.ReposAccess;

/**
 * @author Kariem Hussein
 */
public class UnsupportedRepositoryException extends RepositoryException {

	/**
	 * @param message
	 *            the message
	 * @param access
	 *            the repository
	 * @param cause
	 *            the cause of the exception
	 */
	public UnsupportedRepositoryException(String message, ReposAccess access,
			Throwable cause) {
		super(message, access, cause);
	}

	/**
	 * @param message
	 *            the message
	 * @param access
	 *            the repository
	 */
	public UnsupportedRepositoryException(String message, ReposAccess access) {
		this(message, access, null);
	}

}
