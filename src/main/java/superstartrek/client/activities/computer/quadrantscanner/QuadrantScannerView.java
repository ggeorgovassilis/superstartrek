package superstartrek.client.activities.computer.quadrantscanner;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
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
	List<Element> phaserElements = new ArrayList<>();

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
		p.getElement().setAttribute("sadasd", "asdasd");
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
			int dy = getElement().getAbsoluteTop();
			int dx = getElement().getAbsoluteLeft();
			presenter.onSectorSelected(x, y, dx+e.getOffsetLeft(), dy+e.getOffsetTop());
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

	@Override
	public void drawBeamBetween(int x1, int y1, int x2, int y2, String colour) {
		Element e1 = eSectors[x1][y1];
		Element e2 = eSectors[x2][y2];
		
		int x1px = e1.getOffsetLeft()+e1.getClientWidth()/2;
		int y1px = e1.getOffsetTop()+e1.getClientHeight()/2;

		int x2px = e2.getOffsetLeft()+e2.getClientWidth()/2;
		int y2px = e2.getOffsetTop()+e2.getClientHeight()/2;

		Element eSvg = presenter.getApplication().browserAPI.createElementNs("http://www.w3.org/2000/svg", "svg");
		eSvg.setAttribute("width", "100%");
		eSvg.setAttribute("height", "100%");
		eSvg.getStyle().setLeft(0, Unit.PX);
		eSvg.getStyle().setTop(0, Unit.PX);
		eSvg.getStyle().setProperty("pointerEvents", "none");
//		eSvg.setAttribute("width", ""+(Math.abs(x1px-x2px)));
//		eSvg.setAttribute("height", ""+Math.abs(y1px-y2px));
		eSvg.setInnerHTML("<line x1='"+x1px+"px' y1='"+y1px+"px' x2='"+x2px+"px' y2='"+y2px+"px' stroke='"+colour+"'/>");
		eSvg.getStyle().setPosition(Position.ABSOLUTE);
		getElement().appendChild(eSvg);
		phaserElements.add(eSvg);
	}

	@Override
	public void clearPhaserMarks() {
		for (Element e:phaserElements)
			e.removeFromParent();
		phaserElements.clear();
	}

}
