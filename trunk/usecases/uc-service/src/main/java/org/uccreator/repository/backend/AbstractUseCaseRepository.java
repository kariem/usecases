// $Id$
package org.uccreator.repository.backend;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.uccreator.model.DocumentInstance;
import org.uccreator.model.Revision;
import org.uccreator.model.RevisionStream;
import org.uccreator.repository.EntryLocation;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.RepositoryException;
import org.uccreator.repository.RepositoryLocation;
import org.uccreator.repository.UseCaseRepository;


/**
 * Abstract implementation of {@link UseCaseRepository} with required properties
 * and simple {@link #compareTo(UseCaseRepository)} method.
 * 
 * @author Kariem Hussein
 */
public abstract class AbstractUseCaseRepository implements UseCaseRepository,
		Comparable<UseCaseRepository> {

	/** Log for use in sub classes */
	protected Log log = Logging.getLog(getClass());

	private String name;
	private String id;

	private String currentPath;

	private List<String> paths;
	private final TreeSet<String> pathSet;

	private boolean pathsValid;

	/** Default constructor */
	public AbstractUseCaseRepository() {
		pathSet = new TreeSet<String>();
	}

	public void addPath(String... path) {
		for (String p : path) {
			pathSet.add(p);
		}
	}

	public void addPaths(List<String> pathsToAdd) {
		pathSet.addAll(pathsToAdd);
	}

	public int compareTo(UseCaseRepository o) {
		return getClass().toString().compareTo(o.getClass().toString());
	}

	public DocumentInstance getContent(String path) throws RepositoryException {
		return getContent(new EntryLocation(path, Revision.LATEST.revision()));
	}

	/**
	 * Default implementation
	 * 
	 * @return a single revision streams with all versions for {@code path} at
	 *         {@code revision}.
	 * @throws RepositoryException
	 *             if an error occurred while building the stream
	 */
	public List<RevisionStream> getRevisionStreams(EntryLocation entryLocation)
			throws RepositoryException {
		RevisionStream stream = buildRevisionStream(entryLocation);
		stream.setName("main");
		return Collections.singletonList(stream);
	}

	/**
	 * @param loc
	 *            the entry's location
	 * @return a stream of all versions for this entry
	 * @throws RepositoryException
	 *             if an error occurred building the stream.
	 */
	protected RevisionStream buildRevisionStream(EntryLocation loc)
			throws RepositoryException {
		// prepare stream
		RepositoryLocation reposLoc = new RepositoryLocation(this, loc.path);
		RevisionStream stream = new RevisionStream(reposLoc);
		// query and add instances
		Collection<DocumentInstance> instances = getHistory(loc);
		stream.addInstances(instances);
		return stream;
	}

	private Collection<DocumentInstance> getHistory(EntryLocation location)
			throws RepositoryException {
		EntryLocation[] locations = getHistoryLocations(location);
		List<DocumentInstance> instances = new ArrayList<DocumentInstance>(
				locations.length);

		for (EntryLocation loc : locations) {
			instances.add(getContent(loc));
		}
		return instances;
	}

	/**
	 * @param location
	 *            the location of the entry
	 * @return a list of all locations of elements in the history of the entry
	 *         identified by {@code location}
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unused")
	protected EntryLocation[] getHistoryLocations(EntryLocation location)
			throws RepositoryException {
		return new EntryLocation[] { location };
	}

	/**
	 * @param t
	 *            the throwable
	 * @param messageTemplate
	 *            the message template
	 * @param params
	 *            parameters for the template
	 * @return the exception to throw
	 */
	protected RepositoryException reposException(Throwable t,
			String messageTemplate, Object... params) {
		log.warn(messageTemplate, t, params);
		return new RepositoryException(t.getMessage(), this, t);
	}

	/**
	 * @param t
	 *            the throwable
	 * @param messageTemplate
	 *            the message template
	 * @param params
	 *            parameters for the template
	 * @return the exception to throw
	 */
	protected RepositoryConnectionException connectionException(Throwable t,
			String messageTemplate, Object... params) {
		log.warn(messageTemplate, t, params);
		return new RepositoryConnectionException(t.getMessage(), this, t);
	}

	/**
	 * Encodes a path to be used in an URI
	 * 
	 * @param path
	 *            the path to encode
	 * @return the encoded path
	 */
	protected String encodeUri(String path) {
		try {
			return URLEncoder.encode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.warn("Could not encode path '#0': #1", e.getMessage());
			return path;
		}
	}

	/**
	 * Normalizes the path with {@link URI#normalize()} and removes trailing
	 * <tt>/</tt> characters or redundant <tt>..</tt>.
	 * 
	 * @param path
	 *            the path to normalize
	 * @return a normalized path that can be used as a parameters to
	 *         {@link UseCaseRepository use case repositories} that do not
	 *         support special characters in the path string.
	 */
	public String normalize(String path) {
		String p = createNormalizedPath(path);
		if (p == null) {
			return null;
		}
		if (!p.equals("/") && p.endsWith("/")) {
			p = p.substring(0, p.length() - 1);
		} else if (p.equals("/..") || p.matches("(/\\.\\.)+")) {
			// replace "/.." or "/../.." or "/../../.." etc. with "/"
			p = "/";
		}
		return p;
	}

	/**
	 * Performs the standard {@link URI} normalization.
	 * 
	 * @param path
	 *            the path to normalize
	 * @return the normalized path.
	 */
	private String createNormalizedPath(String path) {
		if (path == null) {
			return null;
		}
		String p = path;
		while (p.contains("//")) {
			p = p.replace("//", "/");
		}
		try {
			return URI.create(p).normalize().getPath();
		} catch (IllegalArgumentException e) {
			log.warn("Could not convert path to correct URI for #0", path);
			return null;
		}
	}

	// hashcode and equals

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractUseCaseRepository other = (AbstractUseCaseRepository) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	// getters and setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getPaths() {
		if (!pathsValid) {
			paths = Arrays.asList(pathSet.toArray(new String[pathSet.size()]));
		}
		return paths;
	}

	public void setPaths(List<String> paths) {
		pathSet.clear();
		pathSet.addAll(paths);
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	/**
	 * @return the log
	 */
	public Log getLog() {
		return log;
	}

	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(Log log) {
		this.log = log;
	}

}