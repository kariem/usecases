// $Id$
package org.uccreator.web;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.uccreator.repository.EntryLocation;


/**
 * Converter for {@link EntryLocation}
 * 
 * @author Kariem Hussein
 */
@Name("entryLocationConverter")
@org.jboss.seam.annotations.faces.Converter(forClass = EntryLocation.class)
public class EntryLocationConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent c, String s) {
		if (s == null) {
			return null;
		}
		return new EntryLocation(s);
	}

	public String getAsString(FacesContext ctx, UIComponent c, Object s) {
		if (s instanceof EntryLocation) {
			return s.toString();
		}
		return null;
	}

}
