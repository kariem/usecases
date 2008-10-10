// $Id$
package org.uccreator.action;

import java.util.List;

import javax.ejb.Local;

import org.uccreator.repository.UseCaseRepository;


/**
 * Repository list.
 * 
 * @author Kariem Hussein
 */
@Local
public interface RepoList {

	/**
	 * @param dataRepos
	 */
	void add(UseCaseRepository dataRepos);

	/**
	 * @param id
	 *            the id
	 * @return the repository identified by {@code id}, or <code>null</code>
	 *         if no such repository exists
	 */
	UseCaseRepository get(String id);
	
	/** Select a repository */
	void select();

	/**
	 * @return the repositories.
	 */
	List<UseCaseRepository> prepareRepositories();

	/** Destroy method */
	void destroy();
}
