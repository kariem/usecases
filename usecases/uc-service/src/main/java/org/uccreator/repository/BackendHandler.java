// $Id$
package org.uccreator.repository;

import org.uccreator.content.Processor;
import org.uccreator.model.ReposAccess;

/**
 * @author Kariem Hussein
 */
public interface BackendHandler extends Processor<String, UseCaseRepository> {

	/**
	 * Connects to the repository
	 * 
	 * @param access
	 *            the repository to connect to
	 * @return a data repository that abstracts the information in {@code repos}
	 * @throws RepositoryConnectionException
	 *             if the connection could not be set up.
	 */
	UseCaseRepository connect(ReposAccess access)
			throws RepositoryConnectionException;

}
