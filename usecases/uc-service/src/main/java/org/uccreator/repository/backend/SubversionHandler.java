// $Id$
package org.uccreator.repository.backend;

import java.net.URL;

import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.uccreator.model.ReposAccess;
import org.uccreator.repository.BackendHandler;
import org.uccreator.repository.RepositoryAuthenticationException;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.UseCaseRepository;


/**
 * @author Kariem Hussein
 */
public class SubversionHandler extends AbstractBackendHandler implements
		BackendHandler {

	boolean initialized;

	private void init() {
		if (initialized) {
			return;
		}
		DAVRepositoryFactory.setup();
		FSRepositoryFactory.setup();
		initialized = true;
	}

	@Override
	protected boolean checkUrl(URL u) {
		String protocol = u.getProtocol();
		return protocol.startsWith("http") || protocol.equals("svn")
				|| protocol.equals("file");
	}

	public UseCaseRepository connect(ReposAccess reposAccess)
			throws RepositoryConnectionException {
		init();

		String name = reposAccess.getUsername();
		String password = reposAccess.getPassword();

		SVNURL svnurl = parseUrl(reposAccess);
		SVNRepository repository = authenticate(name, password, svnurl,
				reposAccess);

		AbstractUseCaseRepository r = new SubversionRepository(repository);
		debug("Created SVN repository: #0; id: #1", r.getName(), r.getId());
		return r;
	}

	private SVNRepository authenticate(String username, String password,
			SVNURL url, ReposAccess repos)
			throws RepositoryAuthenticationException,
			RepositoryConnectionException {
		try {
			SVNRepository repository = SVNRepositoryFactory.create(url);
			if (username != null) {
				ISVNAuthenticationManager authManager = SVNWCUtil
						.createDefaultAuthenticationManager(username, password);
				repository.setAuthenticationManager(authManager);
			}
			repository.testConnection();
			return repository;
		} catch (SVNAuthenticationException e) {
			String message = e.getMessage();
			warn("Authentication error for #0: #1", e, url, message);
			throw new RepositoryAuthenticationException(message, repos, e);
		} catch (SVNException e) {
			String message = e.getMessage();
			warn("Connection error for #0: #1", e, url, message);
			throw new RepositoryConnectionException(message, repos, e);
		} catch (Exception e) {
			String message = e.getMessage();
			warn("Error while trying to connect to #0: #1", e, url, message);
			throw new RepositoryConnectionException(message, repos, e);
		}
	}

	private SVNURL parseUrl(ReposAccess repos)
			throws RepositoryConnectionException {
		String url = repos.getUrl();
		try {
			return SVNURL.parseURIDecoded(url);
		} catch (SVNException e) {
			String message = e.getMessage();
			warn("Could not parse SVN URL from #0: #1", url, message, e);
			throw new RepositoryConnectionException(message, repos, e);
		}
	}

}
