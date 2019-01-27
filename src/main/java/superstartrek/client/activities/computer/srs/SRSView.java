package superstartrek.client.activities.computer.srs;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.Presenter;

public class SRSView extends BaseView<SRSActivity> {

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
				eTR.appendChild(eTD);
			}
			e.appendChild(eTR);
		}
		return table;
	}
	
	public void updateCell(int x, int y, String symbol, String css) {
		eCells[x][y].setInnerText(symbol);
		eCells[x][y].setClassName(css);
	}

	public SRSView(Presenter<SRSActivity> presenter) {
		super(presenter);
	}

}
