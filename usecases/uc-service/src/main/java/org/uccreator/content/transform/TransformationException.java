// $Id$
package org.uccreator.content.transform;

import org.uccreator.content.ProcessingException;

/**
 * @author Kariem Hussein
 */
public class TransformationException extends ProcessingException {

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause of the exception
	 */
	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public TransformationException(String message) {
		this(message, null);
	}
}
