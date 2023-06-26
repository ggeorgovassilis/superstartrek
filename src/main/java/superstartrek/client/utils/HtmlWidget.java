package superstartrek.client.utils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

//framework requirement that this implements HasWidgets
public abstract class HtmlWidget extends Widget {
	
	public HtmlWidget() {
	}

	public void replaceElementWithMyself(String elementId) {
		if (isAttached())
			throw new RuntimeException("Widget already attached");
		Element eToBeReplaced = DOM.getElementById(elementId);
		Element parent = eToBeReplaced.getParentElement();
		parent.replaceChild(getElement(), eToBeReplaced);
		onAttach();
	}

}
