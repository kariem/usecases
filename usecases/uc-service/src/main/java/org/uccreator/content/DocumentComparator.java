// $Id$
package org.uccreator.content;

import org.uccreator.content.compare.DocumentComparison;
import org.uccreator.model.DocumentInstance;

/**
 * @author Kariem Hussein
 */
public interface DocumentComparator extends
		Processor<DocumentComparison, DocumentInstance> {

	/**
	 * @param comparison
	 *            the information for comparison
	 * @return a newly created instance with all differences incorporated.
	 * @throws ProcessingException
	 *             if an error occurred while trying to compare the instances.
	 * @see Processor#process(Object)
	 */
	DocumentInstance process(DocumentComparison comparison)
			throws ProcessingException;

}
