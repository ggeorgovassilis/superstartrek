package superstartrek.client.activities;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;

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
}