package superstartrek.client.activities.computer.srs;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.Presenter;
import superstartrek.client.activities.lrs.LRSPresenter;
import superstartrek.client.utils.Strings;

public class SRSView extends BaseView<SRSActivity> implements MapCellRenderer{

	Element[][] eCells;
	
	@Override
	protected HTMLPanel createPanel() {
		eCells = new Element[3][3];
		HTMLPanel table = new HTMLPanel("<table id='shortrangescan'><tbody id='xxxyyy'></tbody></table>");
		Element e = table.getElementById("xxxyyy");
		for (int y = 0; y < 3; y++) {
			Element eTR = DOM.createTR();
			for (int x = 0; x < 3; x++) {
				Element eTD = DOM.createTD();
				eCells[x][y] = eTD;
				eTD.setAttribute("dx", ""+(x-1));
				eTD.setAttribute("dy", ""+(y-1));
				eTR.appendChild(eTD);
			}
			e.appendChild(eTR);
		}
		return table;
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Element eTd = event.getNativeEvent().getEventTarget().cast();
				GWT.log(""+eTd.getAttribute("dx"));
				if (!Strings.isEmpty(eTd.getAttribute("dx"))) {
					int dx = Integer.parseInt(eTd.getAttribute("dx"));
					int dy = Integer.parseInt(eTd.getAttribute("dy"));
					((SRSPresenter)getPresenter()).quadrantWasClicked(dx,dy);
				}
			}
		}, ClickEvent.getType());

	}
	
	@Override
	public void updateCell(int x, int y, String symbol, String css) {
		eCells[x][y].setInnerText(symbol);
		eCells[x][y].setClassName(css);
	}

	public SRSView(Presenter<SRSActivity> presenter) {
		super(presenter);
	}

}
