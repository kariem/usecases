// $Id$
package org.uccreator.content.compare;

import java.util.List;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.uccreator.content.Processor;


/**
 * Base class for XML-based document diffing.
 * 
 * @author Kariem Hussein
 */
public abstract class XmlDocumentDiffer implements
		Processor<String[], List<AnnotationDiff>> {

	/** The logger */
	protected Log log = Logging.getLog(getClass());
	
	public boolean canProcess(String[] input) {
		return input.length == 2;
	}

	/**
	 * @return whether this differ produces namespace aware xpath annotations
	 */
	public abstract boolean isNamespaceAware();
}