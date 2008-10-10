// $Id$
package org.uccreator.repository;

/**
 * @author Kariem Hussein
 */
public class RepositoryConnectionException extends RepositoryException {

	/**
	 * @param message
	 *            the message
	 * @param repos
	 *            the repository
	 * @param cause
	 *            the cause of the exception
	 */
	public RepositoryConnectionException(String message, Object repos,
			Throwable cause) {
		super(message, repos, cause);
	}

	/**
	 * @param message
	 *            the message
	 * @param repos
	 *            the repository
	 */
	public RepositoryConnectionException(String message, Object repos) {
		this(message, repos, null);
	}

}
