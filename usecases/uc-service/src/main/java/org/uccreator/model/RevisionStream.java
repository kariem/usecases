// $Id$
package org.uccreator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.uccreator.repository.RepositoryLocation;


/**
 * Series of document instances for a specific {@link RepositoryLocation}.
 * 
 * <ul>
 * <li>Instances of a document in <tt>trunk</tt></li>
 * <li>Instances of a document in <tt>branches/$branch_name</li>
 * </ul>
 * @author Kariem Hussein
 */
public class RevisionStream {

	final RepositoryLocation location;
	final SortedMap<Long, DocumentInstance> instances = new TreeMap<Long, DocumentInstance>();
	String name;

	/**
	 * @param location
	 */
	public RevisionStream(RepositoryLocation location) {
		this.location = location;
	}

	/**
	 * @param instance
	 *            the instance to add
	 */
	public void addInstance(DocumentInstance instance) {
		instances.put(new Long(instance.revision), instance);
	}

	/**
	 * @param c
	 *            the instances to add
	 */
	public void addInstances(Collection<DocumentInstance> c) {
		for (DocumentInstance i : c) {
			addInstance(i);
		}
	}

	/**
	 * @param revision
	 *            the key of the instance
	 * @return the instance
	 */
	public DocumentInstance loadInstance(long revision) {
		if (revision == Revision.LATEST.revision()) {
			return instances.get(instances.lastKey());
		} else if (revision == Revision.FIRST.revision()) {
			return instances.get(instances.firstKey());
		}
		DocumentInstance instance = instances.get(new Long(revision));
		return instance;
	}

	/**
	 * @return a list of the instances of this stream
	 */
	public List<DocumentInstance> getInstances() {
		return new ArrayList<DocumentInstance>(instances.values());
	}

	// simple getters and setters
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the location
	 */
	public RepositoryLocation getLocation() {
		return location;
	}
}
