package superstartrek.client.activities.computer.quadrantscanner;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.CSS;

public class QuadrantScannerView extends BaseView<QuadrantScannerActivity> {

	Element[][] eSectors = new Element[8][8];
	
	public void deselectSectors() {
		for (int x=0;x<8;x++)
		for (int y=0;y<8;y++)
			CSS.removeClass(eSectors[x][y], "selected");
	}
	
	public void selectSector(int x, int y) {
		CSS.addClass(eSectors[x][y], "selected");
	}
	
	public void updateSector(int x, int y, String content, String css) {
		eSectors[x][y].setInnerHTML(content);
		eSectors[x][y].setClassName(css);
	}
	
	@Override
	protected HTMLPanel createPanel() {
		
		HTMLPanel p = new HTMLPanel("<table id=quadrantscan></table>");
		Event.sinkEvents(p.getElementById("quadrantscan"), Event.ONCLICK);
		return p;
	}
	
	public QuadrantScannerView(QuadrantScannerPresenter presenter) {
		super(presenter);
		Element e = panel.getElementById("quadrantscan");
		for (int y = 0; y < 8; y++) {
			Element eTr = DOM.createTR();
			for (int x = 0; x < 8; x++) {
				Element eTd = DOM.createTD();
				eTd.setAttribute("x", "" + x);
				eTd.setAttribute("y", "" + y);
				eTr.appendChild(eTd);
				eSectors[x][y] = eTd;
			}
			e.appendChild(eTr);
		}
		panel.addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element e = event.getNativeEvent().getEventTarget().cast();
				int x = Integer.parseInt(e.getAttribute("x"));
				int y = Integer.parseInt(e.getAttribute("y"));
				presenter.onSectorSelected(x, y, event.getClientX(), event.getClientY());
			}
		}, ClickEvent.getType());
	}
	
	public void setQuadrantHeader(String name, String css){
		Element e = DOM.getElementById("quadrantheader");
		e.setInnerText(name);
		e.setClassName(css);
	}


}
