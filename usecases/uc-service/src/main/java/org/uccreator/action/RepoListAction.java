// $Id$
package org.uccreator.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.uccreator.repository.UseCaseRepository;


/**
 * @author Kariem Hussein
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("repoList")
@AutoCreate
public class RepoListAction extends BaseAction implements RepoList {

	@DataModel
	List<UseCaseRepository> repositories;
	
	@In(required = false)
	@Out(required = false, scope=ScopeType.CONVERSATION)
	UseCaseRepository repos;
	
	public void add(UseCaseRepository ucRepos) {
		initRepositories();
		String reposId = ucRepos.getId();
		String reposName = ucRepos.getName();
		
		int posOldRepos = repositories.indexOf(ucRepos);
		if (posOldRepos >= 0) {
			UseCaseRepository oldRepos = repositories.get(posOldRepos);
			oldRepos.addPaths(ucRepos.getPaths());
			debug("Repository '#0' already in list of repositories", reposName);
			return;
		}
		repositories.add(ucRepos);
		addFacesMessage("Added new repository #0 (id: #1)", reposName, reposId);
	}
	
	public UseCaseRepository get(String id) {
		initRepositories();
		for (UseCaseRepository r : repositories) {
			if (r.getId().equals(id)) {
				return r;
			}
		}
		return null;
	}
	
	@Begin(join = true)
	public void select() {
		info("Repository selected: #0", repos);
	}

	@Factory("repositories")
	public List<UseCaseRepository> prepareRepositories() {
		initRepositories();
		Collections.sort(repositories);
		return repositories;
	}

	private void initRepositories() {
		if (repositories == null) {
			repositories = new ArrayList<UseCaseRepository>();
		}
	}

	@Remove
	@Destroy
	public void destroy() {
		// nothing to remove
	}

}
