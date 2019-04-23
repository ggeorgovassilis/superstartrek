package superstartrek.client.activities.computer.srs;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Strings;

public class SRSView extends BaseView<SRSPresenter> implements ISRSView, ClickHandler {

	Element[][] eCells;

	@Override
	protected Widget createWidgetImplementation() {
		eCells = new Element[3][3];
		HtmlWidget table = new HtmlWidget(DOM.createTable(), "<tbody></tbody>");
		table.getElement().setId("shortrangescan");
		Element e = table.getElement().getElementsByTagName("tbody").getItem(0);
		for (int y = 0; y < 3; y++) {
			Element eTR = DOM.createTR();
			for (int x = 0; x < 3; x++) {
				Element eTD = DOM.createTD();
				eCells[x][y] = eTD;
				eTD.setAttribute("data-dx", "" + (x - 1));
				eTD.setAttribute("data-dy", "" + (y - 1));
				eTR.appendChild(eTD);
			}
			e.appendChild(eTR);
		}
		return table;
	}

	@Override
	public void decorateWidget() {
		super.decorateWidget();
		addDomHandler(this, ClickEvent.getType());
	}

	@Override
	public void updateCell(int x, int y, String symbol, String css) {
		eCells[x][y].setInnerText(symbol);
		eCells[x][y].setClassName(css);
	}

	public SRSView(SRSPresenter presenter) {
		super(presenter);
	}

	@Override
	public void onClick(ClickEvent event) {
		Element e = event.getNativeEvent().getEventTarget().cast();
		if (!Strings.isEmpty(e.getAttribute("data-dx"))) {
			int dx = Integer.parseInt(e.getAttribute("data-dx"));
			int dy = Integer.parseInt(e.getAttribute("data-dy"));
			((SRSPresenter) getPresenter()).quadrantWasClicked(dx, dy);
		}
	}

}
