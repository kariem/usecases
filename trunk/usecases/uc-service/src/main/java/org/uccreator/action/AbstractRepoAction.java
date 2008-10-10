// $Id$
package org.uccreator.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.uccreator.repository.RepositoryException;
import org.uccreator.repository.UseCaseRepository;


/**
 * Base class for actions accessing repository information.
 * 
 * @author Kariem Hussein
 */
public abstract class AbstractRepoAction extends BaseAction {

	@In(required = false)
	@Out(required = false)
	String reposId;

	@In
	protected RepoList repoList;

	@In(required = false)
	@Out(required = false, scope = ScopeType.CONVERSATION)
	protected UseCaseRepository repos;

	/**
	 * Prepares the repository
	 * @throws RepositoryException
	 */
	protected void prepareRepos() throws RepositoryException {
		if (repos == null) {
			repos = repoList.get(reposId);
		}
		if (repos == null) {
			throw new RepositoryException("Could not find repository "
					+ reposId, null);
		}
	}
}