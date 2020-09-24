package superstartrek.client.activities.computer.quadrantscanner;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

import superstartrek.client.utils.Strings;

/** 
 * A wrapper and cache around {@link Element}. Some methods, eg. Element.addClass or Element,removeClass
 * are slow and CPU intensive. DOM operations are slow [citation needed] and should be avoided. The ElementWrapper
 * caches values of interest and minimises interactions with the DOM. ElementWrapper should be used where
 * elements are frequently accessed such as the sector tiles in the quadrant scanner screen.
 */
class ElementWrapper extends JavaScriptObject{

	protected ElementWrapper() {
	}
	
	public native final static ElementWrapper create(Element e)/*-{
		var a = [];
		a.innerHTML = "";
		a.element = e;
		return a;
	}-*/;
	
	protected native final boolean contains(String css)/*-{
		return this.indexOf(css)!=-1;
	}-*/;

	protected native final void add(String css)/*-{
		this.push(css);
	}-*/;

	protected native final boolean remove(String css)/*-{
		var i = this.indexOf(css);
		if (i == -1)
			return false;
		this.splice(i,1);
		return true;
	}-*/;
	
	protected native final Element getElement()/*-{
		return this.element;
	}-*/;
	
	protected native final int getLength()/*-{
		return this.length;
	}-*/;

	protected native final void clearArray()/*-{
		this.length=0;
	}-*/;

	protected native final String get(int index)/*-{
		return this[index];
	}-*/;

	protected native final String _getInnerHTML()/*-{
		return this.innerHTML;
	}-*/;

	protected native final void _setInnerHTML(String innerHTML)/*-{
		this.innerHTML = innerHTML;
	}-*/;

	public final void addClassName(String css) {
		if (contains(css))
			return;
		add(css);
		Element e = getElement();
		String newClassName = e.getClassName();
		if (Strings.isEmpty(newClassName))
			newClassName = css;
		else newClassName+=" "+css;
		e.setClassName(newClassName);
	}
	
	public final void removeClassName(String css){
		if (!remove(css))
			return;
		String s ="";
		String prefix="";
		for (int i=getLength()-1;i>=0;i--,prefix=",") {
			s+=prefix+get(i);
		}
		getElement().setClassName(s);
	};
	
	public final void clear() {
		clearArray();
		_setInnerHTML("");
		Element e = getElement();
		e.setInnerHTML("");
		e.setClassName("");
	}
	
	public final void setInnerHTML(String html) {
		if (!_getInnerHTML().equals(html)) {
			_setInnerHTML(html);
			getElement().setInnerHTML(html);
		}
	}

	public final void setClassName(String cn) {
		Element e = getElement();
		if (e.getClassName().equals(cn))
			return;
			clearArray();
			add(cn);
			getElement().setClassName(cn);
	}
}
