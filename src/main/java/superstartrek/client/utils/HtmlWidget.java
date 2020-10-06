package superstartrek.client.utils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

//framework requirement that this implements HasWidgets
public abstract class HtmlWidget extends Widget {
	
	public HtmlWidget() {
	}
	
	//GWT's HtmlPanel implements this by attaching the element to the running document, using document.getElementById and removing it again
	//I don't like that even if the ID is unique. I'd like an element.getChildById(id) function.

	protected Node getChildWithId(Node root, String id) {
		if (root == null)
			return null;
		//doesn't matter that "root" might not be an element; the cast isn't type-checked at runtime and it's ok if "id" is undefined
		Element e = root.cast();
		if (id.equals(e.getId()))
			return e;
		NodeList<Node> children = root.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node n = children.getItem(i);
			Node found = getChildWithId(n, id);
			if (found != null)
				return found;
		}
		return null;
	}

	public Element getElementById(String id) {
		if (isAttached())
			return DOM.getElementById(id);
		return getChildWithId(getElement(), id).cast();
	}
	
	public void addAndReplaceElement(HtmlWidget widget, String elementId) {
		Element e = getElementById(elementId);
		widget.removeFromParent();
		e.appendChild(widget.getElement());
		widget.onAttach();
	}

}
