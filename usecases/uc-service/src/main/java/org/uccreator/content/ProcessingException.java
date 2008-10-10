// $Id$
package org.uccreator.content;

/**
 * @author Kariem Hussein
 */
public class ProcessingException extends Exception {
	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause of the exception
	 */
	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public ProcessingException(String message) {
		this(message, null);
	}
}
