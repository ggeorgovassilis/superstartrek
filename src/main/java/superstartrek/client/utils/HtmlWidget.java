package superstartrek.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

//framework requirement that this implements HasWidgets
public class HtmlWidget extends Widget {

	public HtmlWidget(Element e) {
		setElement(e);
		if (RootPanel.getBodyElement().isOrHasChild(e))
			onAttach();
	}
	
	public HtmlWidget(Element e, String html) {
		this(e);
		e.setInnerHTML(html);
	}
	
	public Element getElementById(String id) {
		if (isAttached())
			return DOM.getElementById(id);
		Element gp = DOM.getElementById("glasspanel");
		gp.appendChild(getElement());
		Element e = DOM.getElementById(id);
		getElement().removeFromParent();
		return e;
	}

}
