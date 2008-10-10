// $Id$
package org.uccreator;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * @author Kariem Hussein
 * 
 */
public class TestableLog implements Log {

	final Log log;
	boolean logged;

	/**
	 * @param clazz
	 */
	public TestableLog(Class<?> clazz) {
		this.log = Logging.getLog(clazz);
	}

	/**
	 * @param category
	 */
	public TestableLog(String category) {
		this.log = Logging.getLog(category);
	}

	/** Default constructor using this class as category */
	public TestableLog() {
		this(TestableLog.class);
	}

	/**
	 * @return whether this log has logged
	 */
	public boolean hasLogged() {
		return logged;
	}

	/**
	 * @param logged
	 */
	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	// delegate methods may be overridden

	public void debug(Object object, Object... params) {
		log.debug(object, params);
		logged = true;
	}

	public void debug(Object object, Throwable t, Object... params) {
		log.debug(object, t, params);
		logged = true;
	}

	public void error(Object object, Object... params) {
		log.error(object, params);
		logged = true;
	}

	public void error(Object object, Throwable t, Object... params) {
		log.error(object, t, params);
		logged = true;
	}

	public void fatal(Object object, Object... params) {
		log.fatal(object, params);
		logged = true;
	}

	public void fatal(Object object, Throwable t, Object... params) {
		log.fatal(object, t, params);
		logged = true;
	}

	public void info(Object object, Object... params) {
		log.info(object, params);
		logged = true;
	}

	public void info(Object object, Throwable t, Object... params) {
		log.info(object, t, params);
		logged = true;
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return log.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	public void trace(Object object, Object... params) {
		log.trace(object, params);
		logged = true;
	}

	public void trace(Object object, Throwable t, Object... params) {
		log.trace(object, t, params);
		logged = true;
	}

	public void warn(Object object, Object... params) {
		log.warn(object, params);
		logged = true;
	}

	public void warn(Object object, Throwable t, Object... params) {
		log.warn(object, t, params);
		logged = true;
	}

}
