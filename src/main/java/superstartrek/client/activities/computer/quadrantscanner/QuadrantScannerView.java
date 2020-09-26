package superstartrek.client.activities.computer.quadrantscanner;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.model.Constants;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Strings;
import superstartrek.client.utils.Timer;

public class QuadrantScannerView extends BaseView<QuadrantScannerPresenter> implements IQuadrantScannerView {

	ElementWrapper[][] buckets = new ElementWrapper[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
	ElementWrapper bSelectedSector;
	List<Element> beamElements = new ArrayList<>();

	@Override
	public void deselectSectors() {
		bSelectedSector.removeClassName("selected");
	}

	@Override
	public void selectSector(int x, int y) {
		bSelectedSector = buckets[x][y];
		bSelectedSector.addClassName("selected");
	}

	@Override
	protected boolean alignsOnItsOwn() {
		return true;
	}

	@Override
	public void updateSector(int x, int y, String content, String css) {
		ElementWrapper b = buckets[x][y];
		b.setInnerHTML(content);
		b.setClassName(css);
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
		Element eMatrix = widgetImpl.getElement();
		final double RELATIVE_WIDTH = 100.0 / Constants.SECTORS_EDGE;
		final double RELATIVE_HEIGHT = 100.0 / Constants.SECTORS_EDGE;
		for (int y = 0; y < Constants.SECTORS_EDGE; y++) {
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				Element eSector = DOM.createDiv();
				eSector.setAttribute("data-x", "" + x);
				eSector.setAttribute("data-y", "" + y);
				eSector.getStyle().setLeft(RELATIVE_WIDTH * (double) x, Unit.PCT);
				eSector.getStyle().setTop(RELATIVE_HEIGHT * (double) y, Unit.PCT);
				buckets[x][y] = ElementWrapper.create(eSector);
				eMatrix.appendChild(eSector);
			}
		}
		widgetImpl.addDomHandler((event) -> handleClick(event), MouseDownEvent.getType());
		widgetImpl.addDomHandler((event) -> handleClick(event), TouchStartEvent.getType());
		bSelectedSector = buckets[0][0];
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
			presenter.onSectorSelected(x, y, dx + e.getOffsetLeft(), dy + e.getOffsetTop());
			event.stopPropagation();
			event.preventDefault();
		} catch (Exception ex) {
			// can happen when user clicks on the borders between cells, which don't have
			// x/y attributes
		}

	}

	@Override
	public void removeCssFromCell(int x, int y, String css) {
		buckets[x][y].removeClassName(css);
	}

	@Override
	public void addCssToCell(int x, int y, String css) {
		buckets[x][y].addClassName(css);
	}

	@Override
	public int getHorizontalOffsetOfSector(int x, int y) {
		return buckets[x][y].getElement().getAbsoluteLeft();
	}

	@Override
	public int getVerticalOffsetOfSector(int x, int y) {
		return buckets[x][y].getElement().getAbsoluteTop();
	}

	@Override
	public void drawBeamBetween(int x1, int y1, int x2, int y2, String colour) {
		Element e1 = buckets[x1][y1].getElement();
		Element e2 = buckets[x2][y2].getElement();

		int x1px = e1.getOffsetLeft() + e1.getClientWidth() / 2;
		int y1px = e1.getOffsetTop() + e1.getClientHeight() / 2;

		int x2px = e2.getOffsetLeft() + e2.getClientWidth() / 2;
		int y2px = e2.getOffsetTop() + e2.getClientHeight() / 2;

		Element eSvg = presenter.getApplication().browserAPI.createElementNs("http://www.w3.org/2000/svg", "svg");
		eSvg.setAttribute("width", "100%");
		eSvg.setAttribute("height", "100%");
		eSvg.getStyle().setLeft(0, Unit.PX);
		eSvg.getStyle().setTop(0, Unit.PX);
		eSvg.getStyle().setProperty("pointerEvents", "none");
//		eSvg.setAttribute("width", ""+(Math.abs(x1px-x2px)));
//		eSvg.setAttribute("height", ""+Math.abs(y1px-y2px));
		eSvg.setInnerHTML("<line x1='" + x1px + "px' y1='" + y1px + "px' x2='" + x2px + "px' y2='" + y2px
				+ "px' stroke='" + colour + "'/>");
		eSvg.getStyle().setPosition(Position.ABSOLUTE);
		getElement().appendChild(eSvg);
		beamElements.add(eSvg);
	}

	@Override
	public void clearBeamMarks() {
		for (Element e : beamElements)
			e.removeFromParent();
		beamElements.clear();
	}

	@Override
	public void animateTorpedoFireBetween(int x1, int y1, int x2, int y2, Callback<Void> callback) {
		Element e1 = buckets[x1][y1].getElement();
		Element e2 = buckets[x2][y2].getElement();

		int x1px = e1.getOffsetLeft() + e1.getClientWidth() / 2;
		int y1px = e1.getOffsetTop() + e1.getClientHeight() / 2;

		int x2px = e2.getOffsetLeft() + e2.getClientWidth() / 2;
		int y2px = e2.getOffsetTop() + e2.getClientHeight() / 2;

		Element eTorpedo = DOM.createSpan();
		Style s = eTorpedo.getStyle();
		s.setLeft(x1px, Unit.PX);
		s.setTop(y1px, Unit.PX);
		eTorpedo.setClassName("torpedo-dot");
		getElement().appendChild(eTorpedo);
		int dx = x2px - x1px;
		int dy = y2px - y1px;
		int gap_ms = 100;
		int animation_duration_ms = 100;
		Timer.postpone(() -> {
			s.setProperty("transform", "translate(" + dx + "px," + dy + "px)");
			s.setProperty("transition", "linear " + (animation_duration_ms / 1000) + "s");
		}, gap_ms);
		Timer.postpone(() -> {
			eTorpedo.removeFromParent();
			callback.onSuccess(null);
		}, gap_ms + animation_duration_ms);
	}

	@Override
	public void clearSector(int x, int y) {
		ElementWrapper b = buckets[x][y];
		b.clear();
	}

}
