// $Id$
package org.uccreator.content.transform;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.uccreator.content.AbstractContentProcessor;
import org.uccreator.content.ContentProcessor;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.Processor;
import org.uccreator.model.DocumentInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * {@link ContentProcessor} that provides XML-based transformation.
 * 
 * @author Kariem Hussein
 */
public class TransformerBasedProcessor extends AbstractContentProcessor {

	private Map<Processor<Document, ?>, String> styles;
	private List<Processor<Node, Node>> postProcessors;

	public String process(DocumentInstance instance) throws ProcessingException {
		String asText;
		try {
			asText = instance.getTextContents();
		} catch (UnsupportedEncodingException e) {
			return "Encoding not supported: " + e.getMessage();
		}

		if (!instance.isText()) {
			throw new ProcessingException(
					"Content could not be detected as text.");
		}

		// parse document for input
		Document parsed = getDocument(asText);
		// get appropriate transformer
		Transformer transformer = getTransformer(getStylesheetLocation(parsed));
		transformer.setErrorListener(new LogErrorListener(getLog()));

		// prepare source and result for transformation
		Source source = new DOMSource(parsed);
		DOMResult result = new DOMResult();

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new TransformationException(
					"Could not perform transformation", e);
		}

		Node node = result.getNode();
		node = postProcess(node);
		return toString(node);
	}

	private Node postProcess(Node node) throws ProcessingException {
		if (postProcessors == null) {
			return node;
		}
		Node newNode = node;
		for (Processor<Node, Node> p : postProcessors) {
			newNode = p.process(newNode);
		}
		return newNode;
	}

	private String getStylesheetLocation(Document doc)
			throws TransformationException {
		if (styles == null || styles.isEmpty()) {
			throw new TransformationException(
					"No transformation rules configured");
		}

		for (Map.Entry<Processor<Document, ?>, String> e : styles.entrySet()) {
			Processor<Document, ?> processor = e.getKey();
			if (processor.canProcess(doc)) {
				return e.getValue();
			}
		}
		throw new TransformationException("Could find appropriate transformer");
	}

	// getters and setters

	/**
	 * @return the styles
	 */
	public Map<Processor<Document, ?>, String> getStyles() {
		return styles;
	}

	/**
	 * @param styles
	 *            the styles to set
	 */
	public void setStyles(Map<Processor<Document, ?>, String> styles) {
		this.styles = styles;
	}

	/**
	 * @return the postProcessors
	 */
	public List<Processor<Node, Node>> getPostProcessors() {
		return postProcessors;
	}

	/**
	 * @param postProcessors
	 *            the postProcessors to set
	 */
	public void setPostProcessors(List<Processor<Node, Node>> postProcessors) {
		this.postProcessors = postProcessors;
	}
}