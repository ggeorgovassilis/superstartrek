package superstartrek.client.activities.computer.srs;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import superstartrek.client.activities.BaseView;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;

public class SRSViewImpl extends BaseView<SRSPresenter> implements SRSView{

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
				eTD.setAttribute("data-uih", "s_"+(x - 1)+"_"+(y - 1));
				eTR.appendChild(eTD);
			}
			e.appendChild(eTR);
		}
		setElement(eTable);
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		presenter.getApplication().eventBus.addHandler(Events.INTERACTION, tag->{
			if (!tag.startsWith("s_"))
				return;
			String parts[] = tag.split("_");
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			presenter.quadrantWasClicked(x, y);
		});
	}

	@Override
	public void updateCell(int x, int y, String symbol, String css) {
		Element eCell = eCells[x][y];
		eCell.setInnerText(symbol);
		eCell.setClassName(css);
	}

	public SRSViewImpl(SRSPresenter presenter) {
		super(presenter);
	}

}
