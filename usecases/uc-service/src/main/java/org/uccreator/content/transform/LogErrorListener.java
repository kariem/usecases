// $Id$
package org.uccreator.content.transform;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.jboss.seam.log.Log;

/**
 * Simple {@link ErrorListener} that uses a log as output for warning and error
 * messages.
 * 
 * @author Kariem Hussein
 */
public class LogErrorListener implements ErrorListener {

	private final Log log;

	/**
	 * Default constructor
	 * 
	 * @param log
	 *            the log to use for warning and error messages.
	 */
	public LogErrorListener(Log log) {
		this.log = log;
	}

	public void error(TransformerException exception)
			throws TransformerException {
		log.error("[transform]: #0", exception.getMessage());
	}

	public void fatalError(TransformerException exception)
			throws TransformerException {
		log.fatal("[transform]: #0", exception.getMessage());
	}

	public void warning(TransformerException exception)
			throws TransformerException {
		log.warn("[transform]: #0", exception.getMessage());
	}

}
