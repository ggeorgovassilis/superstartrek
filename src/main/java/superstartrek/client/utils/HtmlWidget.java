package superstartrek.client.utils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

//framework requirement that this implements HasWidgets
public abstract class HtmlWidget extends Widget {
	
	public HtmlWidget() {
	}

	public void replaceElementWithThis(String elementIdToReplace) {
		assert(!isAttached());
		Element eToBeReplaced = Document.get().getElementById(elementIdToReplace);
		Element parent = eToBeReplaced.getParentElement();
		parent.replaceChild(getElement(), eToBeReplaced);
		onAttach();
	}
}