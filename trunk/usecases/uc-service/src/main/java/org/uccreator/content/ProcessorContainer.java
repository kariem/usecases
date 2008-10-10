// $Id$
package org.uccreator.content;

import java.util.List;

/**
 * @author Kariem Hussein
 * @param <I>
 *            the input type
 * @param <O>
 *            the output type
 * @param <T>
 *            the type of processors in this container
 */
public class ProcessorContainer<I, O, T extends Processor<I, O>> implements
		Processor<I, O> {

	/** The children inside this container */
	protected List<T> children;

	public boolean canProcess(I input) {
		return hasChildren();
	}

	public O process(I instance) throws ProcessingException {
		if (hasChildren()) {
			throw new ProcessingException("No child processors configured");
		}

		for (T p : children) {
			if (p.canProcess(instance)) {
				return p.process(instance);
			}
		}

		throw new ProcessingException("No applicable processor found");
	}

	private boolean hasChildren() {
		return children == null || children.isEmpty();
	}

	/**
	 * @return the children
	 */
	public List<T> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<T> children) {
		this.children = children;
	}

}
