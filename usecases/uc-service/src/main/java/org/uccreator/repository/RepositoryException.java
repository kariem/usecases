// $Id$
package org.uccreator.repository;


/**
 * @author Kariem Hussein
 */
public class RepositoryException extends Exception {
	final Object repos;

	/**
	 * @param message
	 *            the message
	 * @param repos
	 *            the repository
	 * @param cause
	 *            the cause of the exception
	 */
	public RepositoryException(String message, Object repos, Throwable cause) {
		super(message, cause);
		this.repos = repos;
	}

	/**
	 * @param message
	 *            the message
	 * @param repos
	 *            the repository
	 */
	public RepositoryException(String message, Object repos) {
		this(message, repos, null);
	}

	/**
	 * @return the repos
	 */
	public Object getRepos() {
		return repos;
	}

}
