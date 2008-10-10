// $Id$
package org.uccreator.action;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import org.uccreator.BaseComponent;


/**
 * @author Kariem Hussein
 */
public abstract class BaseAction extends BaseComponent {

	/**
	 * @param messageTemplate
	 * @param params
	 */
	protected void addFacesMessageWarn(String messageTemplate, Object... params) {
		addFacesMessage(FacesMessage.SEVERITY_WARN, messageTemplate, params);
	}

	/**
	 * @param messageTemplate
	 * @param params
	 */
	protected void addFacesMessageError(String messageTemplate,
			Object... params) {
		addFacesMessage(FacesMessage.SEVERITY_ERROR, messageTemplate, params);
	}

	/**
	 * @param messageTemplate
	 * @param params
	 */
	protected void addFacesMessageFatal(String messageTemplate,
			Object... params) {
		addFacesMessage(FacesMessage.SEVERITY_FATAL, messageTemplate, params);
	}

	/**
	 * @param severity
	 * @param messageTemplate
	 * @param params
	 */
	protected void addFacesMessage(Severity severity, String messageTemplate,
			Object... params) {
		getFacesMessages().add(severity, messageTemplate, params);
	}
}
