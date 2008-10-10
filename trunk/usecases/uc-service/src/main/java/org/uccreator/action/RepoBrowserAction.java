// $Id$
package org.uccreator.action;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.uccreator.model.ReposAccess;
import org.uccreator.repository.BackendCatalog;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.RepositoryEntry;
import org.uccreator.repository.RepositoryException;
import org.uccreator.repository.UnsupportedRepositoryException;


/**
 * @author Kariem Hussein
 */
@Stateless
@Name("repoBrowser")
public class RepoBrowserAction extends AbstractRepoAction implements RepoBrowser {

	@In(required = false)
	ReposAccess reposAccess;

	@In
	BackendCatalog catalog;

	@In(required = false)
	@Out(required = false, scope = ScopeType.CONVERSATION)
	RepositoryEntry reposEntry;

	// browsing
	@In(required = false)
	@Out(required = false)
	String path;

	public String open() {
		if (reposAccess == null) {
			return null;
		}
		return open(reposAccess);
	}

	public String open(ReposAccess access) {
		String url = getUrl(access);
		if (url == null) {
			return null;
		}

		try {
			repos = catalog.connect(access);
		} catch (UnsupportedRepositoryException e) {
			addFacesMessageError(
					"Could not find appropriate backend for #0. Error: #1", access,
					e.getMessage());
		} catch (RepositoryConnectionException e) {
			addFacesMessageError("Could not connect to data repository: #0", e
					.getMessage());
		}

		if (repos != null) {
			repoList.add(repos);
			updatePath();
			return "success";
		}
		return null;
	}

	public void browse() {
		try {
			prepareRepos();
			openPath();
			updatePath();
			debug("Browse completed. Current path: #0, repository entry: #1", path, reposEntry);
		} catch (RepositoryException e) {
			addFacesMessageError("Error while trying to open '#0': #1", path, e
					.getMessage());
		}
	}

	private void openPath() throws RepositoryException {
		if (path == null) {
			path = "/";
		} if (!path.startsWith("/")) {
			path = "/" + path;
		}
		// open the path
		reposEntry = repos.open(path);
	}

	private void updatePath() {
		path = repos.getCurrentPath();
	}

	private String getUrl(ReposAccess access) {
		if (access == null) {
			addFacesMessageWarn("Could not open repository: repository does not contain any information.");
			return null;
		}

		String url = access.getUrl();
		if (url == null || url.trim().length() == 0) {
			addFacesMessageWarn("Could not open repository: repository URL is empty.");
			return null;
		}
		return url;
	}

	public BackendCatalog getCatalog() {
		return catalog;
	}

	public void setCatalog(BackendCatalog catalog) {
		this.catalog = catalog;
	}

}
