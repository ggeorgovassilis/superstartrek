package superstartrek.client.activities.computer.srs;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.BaseView;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.Strings;

public class SRSView extends BaseView<SRSPresenter> implements ISRSView, ClickHandler {

	Element[][] eCells;

	@Override
	protected void createWidgetImplementation() {
		eCells = new Element[3][3];
		Document d = Document.get();
		Element eTable = d.createTableElement();
		eTable.setId("shortrangescan");
		eTable.appendChild(d.createTBodyElement());
		Element e = eTable.getElementsByTagName("tbody").getItem(0);
		for (int y = 0; y < 3; y++) {
			Element eTR = d.createTRElement();
			for (int x = 0; x < 3; x++) {
				Element eTD = d.createTDElement();
				eCells[x][y] = eTD;
				eTD.setAttribute("data-dx", "" + (x - 1));
				eTD.setAttribute("data-dy", "" + (y - 1));
				eTR.appendChild(eTD);
			}
			e.appendChild(eTR);
		}
		setElement(eTable);
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
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
			presenter.quadrantWasClicked(dx, dy);
		}
	}

}
