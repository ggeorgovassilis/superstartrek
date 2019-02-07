package superstartrek.client.activities;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class CSS {

	public static Element removeClass(Element e, String c) {
		String css = e.getAttribute("class");
		String newCss = css.replaceAll(" "+c+" ", " ").replaceAll("^"+c+"$","").replaceAll("^"+c+" ", " ").replaceAll(" "+c+"$", " ");
		e.setAttribute("class", newCss);
		return e;
	}
	
	public static Element addClass(Element e, String c) {
		String css = e.getAttribute("class");
		if (css == null)
			css="";
		String newCss = css;
		boolean contains = css.equals(c) || css.startsWith(c+" ") || css.endsWith(" "+c) || css.contains(" "+c+" ");
		if (!contains)
			newCss+=" "+c;
		e.setAttribute("class", newCss);
		return e;
	}
	
	/**
	 * Return a CSS damage class
	 * @param value between 0 and 1
	 * @return
	 */
	public static String damageClass(double value) {
		if (value<0.10)
			return "damage-offline";
		if (value<0.50)
			return "damage-bad";
		if (value<0.75)
			return "damage-medium";
		if (value<1)
			return "damage-light";
		return "";
	}
	
	public final static native NodeList<Element> querySelectorAll(String selectors) /*-{
	 return $doc.querySelectorAll(selectors);
	 }-*/;
}