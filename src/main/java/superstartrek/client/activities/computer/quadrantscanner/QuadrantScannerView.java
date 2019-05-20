package superstartrek.client.activities.computer.quadrantscanner;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.model.Constants;
import superstartrek.client.utils.DomUtils;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Strings;

public class QuadrantScannerView extends BaseView<QuadrantScannerPresenter> implements IQuadrantScannerView {

	Element[][] eSectors = new Element[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
	Element eSelectedSector;

	@Override
	public void deselectSectors() {
		eSelectedSector.removeClassName("selected");
	}

	@Override
	public void selectSector(int x, int y) {
		eSelectedSector = eSectors[x][y];
		eSelectedSector.addClassName("selected");
	}

	@Override
	public void updateSector(int x, int y, String content, String css) {
		Element e = eSectors[x][y];
		e.setInnerHTML(content);
		e.setClassName(css);
	}

	@Override
	protected HtmlWidget createWidgetImplementation() {
		HtmlWidget p = new HtmlWidget(DOM.createDiv());
		p.getElement().setId("quadrantscan");
		return p;
	}

	public QuadrantScannerView(QuadrantScannerPresenter presenter) {
		super(presenter);
		Widget widgetImpl = getWidget();
		Element eTable = DomUtils.getTbody(widgetImpl.getElement());
		final double RELATIVE_WIDTH = 100.0 / Constants.SECTORS_EDGE;
		final double RELATIVE_HEIGHT = 100.0 / Constants.SECTORS_EDGE;
		for (int y = 0; y < Constants.SECTORS_EDGE; y++) {
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				Element eTd = DOM.createDiv();
				eTd.setAttribute("data-x", "" + x);
				eTd.setAttribute("data-y", "" + y);
				eTd.getStyle().setLeft(RELATIVE_WIDTH * (double) x, Unit.PCT);
				eTd.getStyle().setTop(RELATIVE_HEIGHT * (double) y, Unit.PCT);
				eSectors[x][y] = eTd;
				eTable.appendChild(eTd);
			}
		}
		widgetImpl.addDomHandler((event) -> handleClick(event), MouseDownEvent.getType());
		widgetImpl.addDomHandler((event) -> handleClick(event), TouchStartEvent.getType());
		eSelectedSector = eSectors[0][0];

	}

	protected void handleClick(DomEvent<?> event) {
		NativeEvent ne = event.getNativeEvent();
		Element e = ne.getEventTarget().cast();
		// clicks on vessel parts need to bubble up to cell
		while (Strings.isEmpty(e.getAttribute("data-x")) && !"quadrantscan".equals(e.getId()))
			e = e.getParentElement();
		try {
			int x = Integer.parseInt(e.getAttribute("data-x"));
			int y = Integer.parseInt(e.getAttribute("data-y"));
			presenter.onSectorSelected(x, y, e.getOffsetLeft(), e.getOffsetTop());
			event.stopPropagation();
			event.preventDefault();
		} catch (Exception ex) {
			// can happen when user clicks on the borders between cells, which don't have
			// x/y attributes
		}

	}

	@Override
	public void removeCssFromCell(int x, int y, String css) {
		eSectors[x][y].removeClassName(css);
	}

	@Override
	public void addCssToCell(int x, int y, String css) {
		eSectors[x][y].addClassName(css);
	}

	@Override
	public int getHorizontalOffsetOfSector(int x, int y) {
		return eSectors[x][y].getAbsoluteLeft();
	}

	@Override
	public int getVerticalOffsetOfSector(int x, int y) {
		return eSectors[x][y].getAbsoluteTop();
	}

}
