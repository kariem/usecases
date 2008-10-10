// $Id$
package org.uccreator;

import java.lang.reflect.Field;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.log.Log;

/**
 * @author Kariem Hussein
 */
public abstract class BaseComponent extends Controller {

	/** log */
	@Logger
	protected Log log;

	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(Log log) {
		this.log = log;
		try {
			Field field = Controller.class.getDeclaredField("log");
			field.setAccessible(true);
			field.set(this, log);
		} catch (Exception e) {
			log.warn("Could not override log field in superclass: #0", e, e
					.getMessage());
		}
	}

}
