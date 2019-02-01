package superstartrek.client.activities.lrs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.Presenter;
import superstartrek.client.activities.glasspanel.GlassPanelPresenter;
import superstartrek.client.utils.Strings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class LRSScreen extends BaseScreen<LRSActivity>{
	
	Element[][] cells;
	
	@Override
	protected HTMLPanel createPanel() {
		return new HTMLPanel(Resources.INSTANCE.lrsScreen().getText());
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		cells = new Element[8][8];
		NodeList<Element> tds = CSS.querySelectorAll("#longrangescan .quadrants td");
		for (int i=tds.getLength()-1;i>=0;i--) {
			Element eTd = tds.getItem(i);
			int x = Integer.parseInt(eTd.getAttribute("x"));
			int y = Integer.parseInt(eTd.getAttribute("y"));
			cells[x][y] = eTd;
		}
		DOM.sinkEvents(getElement(), Event.ONCLICK);
		DOM.setEventListener(getElement(), new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				Element eTd = event.getEventTarget().cast();
				if (!Strings.isEmpty(eTd.getAttribute("x"))) {
					int x = Integer.parseInt(eTd.getAttribute("x"));
					int y = Integer.parseInt(eTd.getAttribute("y"));
					((LRSPresenter)getPresenter()).quadrantWasClicked(x,y);
				}
			}
		});
	}
	
	public LRSScreen(Presenter<LRSActivity> p) {
		super(p);
	}
	
	public void addCss(int x, int y, String css) {
		cells[x][y].addClassName(css);
	}
	
	public void updateQuadrant(int x, int y, String text, String css){
		cells[x][y].setInnerHTML(text);
		cells[x][y].setClassName(css);
	}
}
