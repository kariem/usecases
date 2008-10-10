// $Id$
package org.uccreator.action;

import java.util.Collections;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.apache.commons.collections.map.LRUMap;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.uccreator.content.DocumentComparator;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.compare.DocumentComparison;
import org.uccreator.model.DocumentInstance;
import org.uccreator.model.Revision;
import org.uccreator.repository.EntryLocation;
import org.uccreator.repository.RepositoryException;


/**
 * Compares two different locations.
 * 
 * @author Kariem Hussein
 */
@Stateful
@Name("entryComparator")
@Scope(ScopeType.CONVERSATION)
public class EntryComparatorAction extends AbstractUseCaseRenderer implements
		EntryComparator {

	@In("old_path")
	String oldPath;
	@In("new_path")
	String newPath;
	@In(value = "old", required = false)
	String oldRev;
	@In(value = "new", required = false)
	String newRev;

	@Out
	EntryLocation locOld;
	@Out
	EntryLocation locNew;

	@In
	DocumentComparator documentComparator;

	Map<ComparisonKey, DocumentInstance> instances;

	public void prepareInstance() {
		try {
			prepareRepos();
		} catch (RepositoryException e) {
			addFacesMessageError(e.getMessage());
			return;
		}
		prepareLocation();
		getInstance();
	}

	private void getInstance() {
		instance = fromCache(locOld, locNew);
		if (instance != null) {
			return;
		}

		debug("Comparing #0 and #1", locOld, locNew);
		try {
			DocumentInstance iOld = repos.getContent(locOld);
			DocumentInstance iNew = repos.getContent(locNew);
			DocumentComparison comparison = new DocumentComparison(iOld, iNew);
			instance = documentComparator.process(comparison);
			toCache(instance, locOld, locNew);
		} catch (RepositoryException e) {
			String msg = "Error while trying to retrieve revisions #0 and #1: #2";
			error(msg, e, locOld, locNew, e.getMessage());
			addFacesMessageError(msg, locOld, locNew, e.getMessage());
		} catch (ProcessingException e) {
			String msg = "Error while trying to compare #0 and #1: #2";
			error(msg, e, locOld, locNew, e.getMessage());
			addFacesMessageError(msg, locOld, locNew, e.getMessage());
		}
	}

	private void toCache(DocumentInstance di, EntryLocation loc1,
			EntryLocation loc2) {
		initInstances();
		instances.put(new ComparisonKey(loc1, loc2), di);
	}

	@SuppressWarnings("unchecked")
	private void initInstances() {
		if (instances == null) {
			instances = Collections.synchronizedMap(new LRUMap(5));
		}
	}

	private DocumentInstance fromCache(EntryLocation loc1, EntryLocation loc2) {
		if (instances == null) {
			return null;
		}
		return instances.get(new ComparisonKey(loc1, loc2));
	}

	public void compare() {
		prepareInstance();
	}

	private void prepareLocation() {
		locOld = new EntryLocation(oldPath, getRevision(oldRev));
		locNew = new EntryLocation(newPath, getRevision(newRev));
	}

	private long getRevision(String val) {
		if (val == null || val.trim().length() == 0) {
			return Revision.LATEST.revision();
		}

		try {
			return Long.parseLong(val);
		} catch (NumberFormatException e) {
			addFacesMessageWarn(
					"Could not convert #1 to revision parameter, using most recent",
					val);
			return Revision.LATEST.revision();
		}
	}

	@Destroy
	@Remove
	public void destroy() {
		// nothing to do
	}

	class ComparisonKey {
		EntryLocation loc1;
		EntryLocation loc2;

		ComparisonKey(EntryLocation loc1, EntryLocation loc2) {
			this.loc1 = loc1;
			this.loc2 = loc2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + loc1.hashCode();
			result = prime * result + loc2.hashCode();
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
			final ComparisonKey ck = (ComparisonKey) obj;
			return loc1.equals(ck.loc1) && loc2.equals(ck.loc2);
		}

	}
}
