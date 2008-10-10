// $Id$
package org.uccreator.content;

/**
 * @author Kariem Hussein
 * 
 * @param <I>
 *            the type to process
 * @param <O>
 *            the type resulting from the process
 */
public interface Processor<I, O> {

	/**
	 * @param input
	 *            the content to handle
	 * @return <code>true</code> if this processor can handle {@code instance},
	 *         false otherwise
	 */
	boolean canProcess(I input);

	/**
	 * @param input
	 *            the input
	 * @return the processed output
	 * @throws ProcessingException
	 *             if an error occurred during processing.
	 */
	O process(I input) throws ProcessingException;
}
