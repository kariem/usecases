// $Id$
package org.uccreator.repository.backend;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.uccreator.model.ContentType;
import org.uccreator.model.DocumentInstance;
import org.uccreator.model.Revision;
import org.uccreator.repository.EntryLocation;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.RepositoryEntry;
import org.uccreator.repository.RepositoryException;
import org.uccreator.repository.RepositoryLocation;
import org.uccreator.repository.UseCaseRepository;


/**
 * {@link UseCaseRepository} for subversion.
 * 
 * @author Kariem Hussein
 */
public class SubversionRepository extends AbstractUseCaseRepository implements
		UseCaseRepository {

	final SVNRepository repository;
	final SVNURL root;
	SVNURL currentLocation;

	/**
	 * @param repository
	 *            the underlying repository
	 * @throws RepositoryConnectionException
	 *             if the repository could not be initialized
	 */
	public SubversionRepository(SVNRepository repository)
			throws RepositoryConnectionException {
		this.repository = repository;
		this.currentLocation = repository.getLocation();

		try {
			this.root = repository.getRepositoryRoot(false);
			setId(repository.getRepositoryUUID(false));
		} catch (SVNException e) {
			throw new RepositoryConnectionException(
					"Could not create repository", repository, e);
		}

		setName(root.toString());
		updatePath();
	}

	public RepositoryEntry open(String path) throws RepositoryException {
		if (path == null) {
			return null;
		}
		String normalized = normalize(path);
		ContentType type = determineContentType(normalized);

		SVNURL updatedLocation = null;
		try {
			updatedLocation = updateLocation(normalized);
		} catch (SVNException e) {
			throw connectionException(e,
					"Could not change path from '#0' to '#1'",
					getCurrentPath(), normalized);
		}

		// cannot be null now
		assert updatedLocation != null;

		try {
			// no errors -> change location
			repository.setLocation(updatedLocation, false);
		} catch (SVNException e) {
			throw connectionException(e, "Error while trying to open '#0'",
					updatedLocation.getPath());
		}

		// update internal representation
		currentLocation = updatedLocation;

		String pathFromRoot = updatePath();
		String entryName = extractName(pathFromRoot);
		RepositoryEntry entry = createReposEntry(entryName, pathFromRoot, type);
		return entry;
	}

	private SVNURL updateLocation(String path) throws SVNException {
		if (path.equals(currentLocation.getPath())) {
			return currentLocation;
		}
		if (path.startsWith("/")) {
			// absolute path
			return root.appendPath(path, true);
		}
		if (path.equals("..")) {
			return currentLocation.removePathTail();
		}
		return currentLocation.appendPath("/" + path, true);
	}

	public DocumentInstance getContent(EntryLocation loc)
			throws RepositoryException {
		Map<String, Object> props = new HashMap<String, Object>();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Long rev = new Long(loc.revision);

		log.debug("Trying to get contents for #0@#1", loc.path, rev);

		try {
			long revision = repository.getFile(loc.path, loc.revision, props, bos);
			// update revision from repository
			rev = new Long(revision);
		} catch (SVNException e) {
			throw reposException(e, "Could not get '#0' at revision #1",
					loc.path, rev);
		}

		DocumentInstance doc = new DocumentInstance(loc.toString());
		doc.setProperties(props);
		doc.setRevision(rev.longValue());
		doc.setOutputStream(bos);
		String mimeType = (String) props.get(SVNProperty.MIME_TYPE);
		doc.setText(SVNProperty.isTextMimeType(mimeType));
		log.debug("Created document instance for #0@#1 with MIME type #2",
				loc.path, rev, mimeType);

		return doc;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntryLocation[] getHistoryLocations(EntryLocation loc)
			throws RepositoryException {
		Long rev = new Long(loc.revision);
		log.debug("Trying to get history for #0@#1", loc.path, rev);

		long from = Revision.FIRST.revision();
		long to = Revision.LATEST.revision();
		Collection<SVNLogEntry> logEntries = null;

		try {
			logEntries = repository.log(new String[] { loc.path }, null, from,
					to, true, true);
		} catch (SVNException e) {
			throw reposException(e,
					"Could not get history of '#0' at revision #1", loc.path,
					rev);
		}

		EntryLocation[] entryLocations = new EntryLocation[logEntries.size()];
		int pos = 0;
		for (SVNLogEntry e : logEntries) {
			EntryLocation entryLoc = new EntryLocation(loc.path, e
					.getRevision());
			Map changedPaths = e.getChangedPaths();
			if (changedPaths != null) {
				log
						.warn(
								"Revision #0 contains changed paths which are currently ignored",
								new Long(e.getRevision()));
			}
			entryLocations[pos++] = entryLoc;
		}

		return entryLocations;
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryEntry> getEntries(String path)
			throws RepositoryException {
		try {
			Collection<SVNDirEntry> entries = repository.getDir(path, -1, null,
					(Collection<SVNDirEntry>) null);
			List<RepositoryEntry> reposEntries = new ArrayList<RepositoryEntry>(
					entries.size());
			for (SVNDirEntry entry : entries) {
				RepositoryEntry reposEntry = createRepositoryEntry(entry, path);
				reposEntries.add(reposEntry);
			}
			return reposEntries;
		} catch (SVNException e) {
			throw connectionException(e, "Could not list contents of '#0': #1",
					repository.getLocation().getPath());
		}
	}

	private String updatePath() {
		String rootPath = root.getPath();
		// get relative path
		String path = currentLocation.getPath();
		path = path.substring(path.indexOf(rootPath) + rootPath.length());
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		addPath(path);
		setCurrentPath(path);
		return path;
	}

	private RepositoryEntry createRepositoryEntry(SVNDirEntry entry,
			String pathToParent) {
		String name = entry.getName();
		String fullPath = pathToParent + "/" + name;
		ContentType contentType = entry.getKind() == SVNNodeKind.DIR ? ContentType.DIRECTORY
				: ContentType.UNKNOWN;
		RepositoryEntry reposEntry = createReposEntry(name, fullPath,
				contentType);
		reposEntry.setLastChangeDate(entry.getDate());

		return reposEntry;
	}

	private RepositoryEntry createReposEntry(String name, String pathFromRoot,
			ContentType type) {
		RepositoryLocation location = new RepositoryLocation(this, pathFromRoot);
		RepositoryEntry entry = new RepositoryEntry(name, location);
		entry.setContentType(type);
		return entry;
	}

	private String extractName(String pathFromRoot) {
		String name;
		if (pathFromRoot.equals("/")) {
			name = "root";
		} else {
			String path = pathFromRoot;
			if (path.endsWith("/")) {
				path = pathFromRoot.substring(0, path.length() - 1);
			}
			name = pathFromRoot.substring(path.lastIndexOf("/") + 1);
		}
		return name;
	}

	private ContentType determineContentType(String path)
			throws RepositoryException {
		ContentType type = ContentType.UNKNOWN;
		SVNNodeKind kind;
		try {
			kind = repository.checkPath(path, -1);
		} catch (SVNException e) {
			String message = e.getMessage();
			log.warn("Error while trying to determine contents at '#0': #1",
					path, message, e);
			throw new RepositoryException(message, this, e);
		}
		if (kind == SVNNodeKind.NONE) {
			throw new RepositoryException("There is no entry at " + path, this);
		} else if (kind == SVNNodeKind.UNKNOWN) {
			throw new RepositoryException(
					"Cannot determine contents of information at " + path, this);
		} else if (kind == SVNNodeKind.DIR) {
			type = ContentType.DIRECTORY;
		}
		return type;
	}

}
