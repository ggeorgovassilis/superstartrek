package superstartrek.client.activities.lrs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Strings;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class LRSScreen extends BaseScreen<LRSPresenter> implements ILRSScreen{
	
	Element[][] cells;
	
	@Override
	protected HtmlWidget createWidgetImplementation() {
		HtmlWidget widget = new HtmlWidget(DOM.createTable(),presenter.getApplication().getResources().lrsScreen().getText());
		widget.getElement().setId("longrangescan");
		return widget;
	}
	
	@Override
	public void decorateScreen() {
		super.decorateWidget();
		cells = new Element[8][8];
		NodeList<Element> tds = CSS.querySelectorAll("#longrangescan .quadrants td");
		for (int i=tds.getLength()-1;i>=0;i--) {
			Element eTd = tds.getItem(i);
			int x = Integer.parseInt(eTd.getAttribute("data-x"));
			int y = Integer.parseInt(eTd.getAttribute("data-y"));
			eTd.setAttribute("tabindex", ""+i);
			cells[x][y] = eTd;
		}
		Element eLrs = DOM.getElementById("longrangescan");
		DOM.sinkEvents(eLrs, Event.ONCLICK);
		DOM.setEventListener(eLrs, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				Element eTd = event.getEventTarget().cast();
				if (!Strings.isEmpty(eTd.getAttribute("data-x"))) {
					int x = Integer.parseInt(eTd.getAttribute("data-x"));
					int y = Integer.parseInt(eTd.getAttribute("data-y"));
					((LRSPresenter)getPresenter()).quadrantWasClicked(x,y);
				}
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
		cells[x][y].setInnerText(text);
		cells[x][y].setClassName(css);
	}

	@Override
	public void focusCell(int x, int y) {
		cells[x][y].focus();
	}
}
