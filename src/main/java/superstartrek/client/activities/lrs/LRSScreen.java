package superstartrek.client.activities.lrs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.space.Constants;
import superstartrek.client.utils.CSS;
import superstartrek.client.utils.Strings;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class LRSScreen extends BaseScreen<LRSPresenter> implements ILRSScreen{
	
	Element[][] cells;
	
	@Override
	protected void createWidgetImplementation() {
		Element e = Document.get().createTableElement();
		setElement(e);
	}
	
	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.lrsScreen().getText());
		element.setId("longrangescan");
		cells = new Element[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
		NodeList<Element> tds = CSS.querySelectorAll("#longrangescan .quadrants td");
		for (int i=tds.getLength()-1;i>=0;i--) {
			Element eTd = tds.getItem(i);
			int x = Integer.parseInt(eTd.getAttribute("data-x"));
			int y = Integer.parseInt(eTd.getAttribute("data-y"));
			eTd.setAttribute("tabindex", ""+i);
			cells[x][y] = eTd;
		}
		Element eLrs = getElementById("longrangescan");
		DOM.sinkEvents(eLrs, Event.ONCLICK);
		DOM.setEventListener(eLrs, (Event event)-> {
				Element eTd = event.getEventTarget().cast();
				if (!Strings.isEmpty(eTd.getAttribute("data-x"))) {
					int x = Integer.parseInt(eTd.getAttribute("data-x"));
					int y = Integer.parseInt(eTd.getAttribute("data-y"));
					presenter.quadrantWasClicked(x,y);
				}
		});
	}
	
	public LRSScreen(LRSPresenter p) {
		super(p);
	}
	
	@Override
	public void addCss(int x, int y, String css) {
		cells[x][y].addClassName(css);
	}
	
	@Override
	public void updateCell(int x, int y, String text, String css){
		Element e = cells[x][y];
		e.setInnerText(text);
		e.setClassName(css);
	}

	@Override
	public void removeCss(int x, int y, String css) {
		cells[x][y].removeClassName(css);
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}
}
