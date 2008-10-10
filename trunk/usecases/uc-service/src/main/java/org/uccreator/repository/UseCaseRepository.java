// $Id$
package org.uccreator.repository;

import java.util.List;

import org.uccreator.model.DocumentInstance;
import org.uccreator.model.RevisionStream;


/**
 * @author Kariem Hussein
 */
public interface UseCaseRepository extends Comparable<UseCaseRepository> {

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(String name);

	/**
	 * @return the id
	 */
	String getId();

	/**
	 * @param id
	 *            the id to set
	 */
	void setId(String id);

	/**
	 * @return the paths
	 */
	List<String> getPaths();

	/**
	 * @param paths
	 *            the paths to set
	 */
	void setPaths(List<String> paths);

	/**
	 * 
	 * @param path
	 *            the paths to add
	 */
	void addPath(String... path);

	/**
	 * @param paths
	 *            the paths to add
	 */
	void addPaths(List<String> paths);

	/**
	 * @param path
	 *            relative to the repository root
	 * @return a single repository entry for {@code path}
	 * @throws RepositoryException
	 */
	RepositoryEntry open(String path) throws RepositoryException;

	/**
	 * @param path
	 *            the path
	 * @return a list of entries for the currently opened location
	 * @throws RepositoryException
	 */
	List<RepositoryEntry> getEntries(String path) throws RepositoryException;

	/**
	 * @return the currentPath
	 */
	String getCurrentPath();

	/**
	 * @param currentPath
	 *            the currentPath to set
	 */
	void setCurrentPath(String currentPath);

	/**
	 * @param path
	 *            the path relative to the repository root
	 * @return the most current content available at {@code path}
	 * @throws RepositoryException
	 * @see #getContent(EntryLocation)
	 */
	DocumentInstance getContent(String path) throws RepositoryException;

	/**
	 * @param location
	 *            the location of the entry in this repository
	 * @return the content available at {@code path} at {@code revision}.
	 * @throws RepositoryException
	 */
	DocumentInstance getContent(EntryLocation location)
			throws RepositoryException;

	/**
	 * @param entryLocation
	 *            the entry location
	 * @return a list of revision streams for {@code path} at {@code revision}.
	 * @throws RepositoryException
	 */
	List<RevisionStream> getRevisionStreams(EntryLocation entryLocation)
			throws RepositoryException;

}
