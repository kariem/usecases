// $Id$
package org.uccreator.content.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Summary of annotations added.
 * 
 * @author Kariem Hussein
 */
public class AnnotationResult {

	int added;
	int removed;
	int adapted;

	boolean modelValid;

	Map<String, Integer> model;
	List<Entry<String, Integer>> list;

	/**
	 * @param type
	 *            the type to add
	 */
	public void addAnnotation(AnnotationDiff.ChangeType type) {
		switch (type) {
		case ADDED:
			added++;
			break;
		case REMOVED:
			removed++;
			break;
		case ADAPTED:
			adapted++;
			break;
		}
		modelValid = false;
	}

	/**
	 * @return whether there are results
	 */
	public boolean isEmpty() {
		return added == 0 && adapted == 0 && removed == 0;
	}

	/**
	 * @param other
	 *            the results to add to this result
	 */
	public void add(AnnotationResult other) {
		if (other == null) {
			return;
		}
		added += other.added;
		removed += other.removed;
		adapted += other.adapted;
		modelValid = false;
	}

	/**
	 * @return a representation of the model for iteration.
	 */
	public List<Map.Entry<String, Integer>> getModel() {
		initModel();
		return list;
	}

	private void initModel() {
		if (modelValid) {
			return;
		}
		model = new HashMap<String, Integer>(3);
		model.put("compare.added", new Integer(added));
		model.put("compare.removed", new Integer(removed));
		model.put("compare.adapted", new Integer(adapted));
		modelValid = true;

		list = new ArrayList<Entry<String, Integer>>(model.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

	}
}
