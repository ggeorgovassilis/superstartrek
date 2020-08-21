package superstartrek.client.activities;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

import superstartrek.client.model.Setting;

public class CSS {

	final static String[] damageClasses = { "damage-offline", "damage-bad", "damage-medium", "damage-light", "" };

	/**
	 * Return a CSS damage class
	 * 
	 * @param value between 0 and 1
	 * @return
	 */
	public static String damageClass(Setting setting) {
		int index = setting.isBroken()?0:(int) Math.floor((damageClasses.length - 1) * setting.health());
		return damageClasses[index];
	}
	
	public static String getOfflineDamageClass() {
		return damageClasses[0];
	}
	
	public static void setEnabled(Element e, boolean enabled) {
		if (enabled) {
			e.removeAttribute("disabled");
			e.removeClassName("disabled");
		} else {
			e.setAttribute("disabled", "");
			e.addClassName("disabled");
		}
	}

	// according to https://caniuse.com/#search=querySelector this is supported in
	// all browsers that are currently around
	public final static native NodeList<Element> querySelectorAll(String selectors) /*-{
																					return $doc.querySelectorAll(selectors);
																					}-*/;
}