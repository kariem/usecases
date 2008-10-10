// $Id$
package org.uccreator.content;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.uccreator.BaseComponent;
import org.uccreator.content.transform.TransformationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * {@link BaseComponent Component} with utility methods for XML-based
 * operations.
 * 
 * @author Kariem Hussein
 */
public abstract class AbstractXmlComponent extends BaseComponent {

	private final TransformerFactory tf;
	private final DocumentBuilderFactory dbf;
	private Transformer toStringTransformer;

	/** Initializes transformer and document builder fractory */
	public AbstractXmlComponent() {
		tf = TransformerFactory.newInstance();
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	}

	/**
	 * Creates a new transformer
	 * 
	 * @param styleLocation
	 *            the location of the stylesheet
	 * @return the newly created transformer
	 * @throws TransformationException
	 *             if the stylesheet could not be found or the transformer could
	 *             not be constructed from the stylesheet.
	 */
	protected Transformer getTransformer(String styleLocation)
			throws TransformationException {
		InputStream is = getClass().getResourceAsStream(styleLocation);
		if (is == null) {
			throw new TransformationException(
					"Could not get stylesheet at location " + styleLocation);
		}
		Source stylesheet = new StreamSource(is);
		try {
			return tf.newTransformer(stylesheet);
		} catch (TransformerConfigurationException e) {
			throw new TransformationException(
					"Could not get transformer for stylesheet at location "
							+ styleLocation, e);
		}
	}

	/**
	 * @param node
	 *            the node to transform
	 * @return textual representation of the node
	 * @throws TransformationException
	 *             if an error occurred during the transformation.
	 */
	protected String toString(Node node) throws TransformationException {
		initToStringTransformer();

		Source source = new DOMSource(node);
		StringWriter out = new StringWriter();
		Result result = new StreamResult(out);
		try {
			toStringTransformer.transform(source, result);
		} catch (TransformerException e) {
			throw new TransformationException(
					"Could not perform toString transformation", e);
		}
		return out.toString();
	}

	/**
	 * @param asText
	 *            text representation of the XML document
	 * @return DOM representation of {@code asText}
	 * @throws TransformationException
	 *             if the document could not be parsed
	 * @see #getDocument(String, boolean)
	 */
	protected Document getDocument(String asText)
			throws TransformationException {
		return getDocument(asText, true);
	}

	/**
	 * @param asText
	 *            text representation of the XML document
	 * @param namespaceAware
	 *            whether the document should be built from a namespace aware
	 *            parser
	 * @return DOM representation of {@code asText}
	 * @throws TransformationException
	 *             if the document could not be parsed
	 */
	protected Document getDocument(String asText, boolean namespaceAware)
			throws TransformationException {
		try {
			DocumentBuilder docBuilder = null;
			if (!namespaceAware) {
				synchronized (dbf) {
					dbf.setNamespaceAware(false);
					docBuilder = dbf.newDocumentBuilder();
					dbf.setNamespaceAware(true);
				}
			} else {
				docBuilder = dbf.newDocumentBuilder();
			}
			Document parsed = docBuilder.parse(new InputSource(
					new StringReader(asText)));
			return parsed;
		} catch (Exception e) {
			throw new TransformationException(
					"Could not parse document into DOM structure", e);
		}
	}

	private void initToStringTransformer() throws TransformationException {
		if (toStringTransformer != null) {
			return;
		}
		try {
			toStringTransformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new TransformationException(
					"Could not create toString transformer", e);
		}
		toStringTransformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		toStringTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		toStringTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
				"yes");
		toStringTransformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
	}

}