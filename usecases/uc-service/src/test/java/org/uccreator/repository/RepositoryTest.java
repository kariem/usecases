// $Id$
package org.uccreator.repository;

import org.jboss.seam.log.Logging;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.BaseTest;
import org.uccreator.model.ReposAccess;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.UseCaseRepository;
import org.uccreator.repository.backend.AbstractBackendHandler;
import org.uccreator.repository.backend.SubversionHandler;


/**
 * @author Kariem Hussein
 */
public abstract class RepositoryTest extends BaseTest {

	/** The default URL used for testing. */
	protected static final String DEFAULT_URL = "http://usecases.googlecode.com/svn/trunk/uc/";
	/** The default path after connecting to {@link #DEFAULT_URL} */
	protected static final String DEFAULT_PATH = "/trunk/uc";
	/** The default path after connecting to {@link #DEFAULT_URL} */
	protected static final String PATH_TO_FILE = "/trunk/uc/style/preview.css";
	/** The default use case for {@link #DEFAULT_URL} */
	protected static final String PATH_TO_UC = "/trunk/uc/usecases.xml";

	@Autowired
	AbstractBackendHandler svnHandler;

	/** Initializes logging for error handling */
	@Before
	public void initializeLogging() {
		svnHandler.setLog(Logging.getLog(SubversionHandler.class));
	}

	/**
	 * Creates a repository from the connection string {@code url}
	 * 
	 * @param url
	 *            the connection string
	 * @return a connected repository
	 * @throws RepositoryConnectionException
	 *             if an error occurred.
	 */
	protected UseCaseRepository connectTo(String url)
			throws RepositoryConnectionException {
		ReposAccess access = new ReposAccess();
		access.setUrl(url);
		UseCaseRepository ucRepos = svnHandler.connect(access);
		return ucRepos;
	}
}
