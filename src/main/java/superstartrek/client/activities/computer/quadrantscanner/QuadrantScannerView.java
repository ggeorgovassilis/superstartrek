package superstartrek.client.activities.computer.quadrantscanner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.CSS;

public class QuadrantScannerView extends BaseView<QuadrantScannerActivity> implements IQuadrantScannerView {

	Element[][] eSectors = new Element[8][8];
	Element eSelectedSector;
	
	@Override
	public void deselectSectors() {
		if (eSelectedSector!=null) {
			eSelectedSector.removeClassName("selected");
		}
	}
	
	@Override
	public void selectSector(int x, int y) {
		eSectors[x][y].addClassName("selected");
	}
	
	@Override
	public void updateSector(int x, int y, String content, String css) {
		Element e = eSectors[x][y];
		e.setInnerText(content);
		e.setClassName(css);
	}
	
	@Override
	protected HTMLPanel createWidgetImplementation() {
		HTMLPanel p = new HTMLPanel("<div id=quadrantscan></div>");
		Event.sinkEvents(p.getElementById("quadrantscan"), Event.ONCLICK);
		return p;
	}
	
	public QuadrantScannerView(QuadrantScannerPresenter presenter) {
		super(presenter);
		HTMLPanel panel = (HTMLPanel)widgetImpl;
		Element e = panel.getElementById("quadrantscan");
		for (int y = 0; y < 8; y++) {
			Element eTr = DOM.createDiv();
			eTr.addClassName("row");
			eTr.getStyle().setTop(12.5*(double)y, Unit.PCT);
			for (int x = 0; x < 8; x++) {
				Element eTd = DOM.createDiv();
				eTd.setAttribute("x", "" + x);
				eTd.setAttribute("y", "" + y);
				eTr.appendChild(eTd);
				eTd.getStyle().setLeft(12.5*(double)x, Unit.PCT);
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
	

}
