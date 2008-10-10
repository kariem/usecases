// $Id$
package org.uccreator.repository;

import java.util.List;

import org.uccreator.model.ReposAccess;


/**
 * @author Kariem Hussein
 */
public class BackendCatalog {

	List<BackendHandler> handlers;

	/**
	 * @param access
	 *            the repository to connect to
	 * @return a data repository for {@code repos}
	 * @throws UnsupportedRepositoryException
	 *             If no appropriate handler could be found for {@code repos}
	 * @throws RepositoryConnectionException
	 *             If the connection could not be established.
	 */
	public UseCaseRepository connect(ReposAccess access)
			throws UnsupportedRepositoryException,
			RepositoryConnectionException {
		if (access == null || access.getUrl().trim().length() == 0) {
			throw new UnsupportedRepositoryException(
					"Cannot connect to empty repository", access);
		}

		BackendHandler handler = getHandler(access);

		return handler.connect(access);
	}

	private BackendHandler getHandler(ReposAccess access)
			throws UnsupportedRepositoryException {
		for (BackendHandler handler : handlers) {
			if (handler.canProcess(access.getUrl())) {
				return handler;
			}
		}
		throw new UnsupportedRepositoryException(
				"Could not find an adequate handler.", access);
	}

	/**
	 * @return the handlers
	 */
	public List<BackendHandler> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers
	 *            the handlers to set
	 */
	public void setHandlers(List<BackendHandler> handlers) {
		this.handlers = handlers;
	}

}
