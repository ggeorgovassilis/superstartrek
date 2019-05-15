package superstartrek.client.activities;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class CSS {

	final static String[] damageClasses = { "damage-offline", "damage-bad", "damage-medium", "damage-light", "" };

	/**
	 * Return a CSS damage class
	 * 
	 * @param value between 0 and 1
	 * @return
	 */
	public static String damageClass(double value) {
		int index = (int) Math.floor((damageClasses.length - 1) * value);
		return damageClasses[index];
	}

	// according to https://caniuse.com/#search=querySelector this is supported in
	// all browsers that are currently around
	public final static native NodeList<Element> querySelectorAll(String selectors) /*-{
																					return $doc.querySelectorAll(selectors);
																					}-*/;
}