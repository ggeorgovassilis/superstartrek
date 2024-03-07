package superstartrek.client.activities.computer.quadrantscanner;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.BaseView;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.space.Constants;

public class QuadrantScannerViewImpl extends BaseView<QuadrantScannerPresenter> implements QuadrantScannerView {

	Element[][] buckets = new Element[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
	Element bSelectedSector;
	List<Element> beamElements = new ArrayList<>();
	Element eSvgProto;

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
		Element b = buckets[x][y];
		b.setInnerHTML(content);
		b.setClassName(css);
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		element.setId("quadrantscan");
	}

	public QuadrantScannerViewImpl(QuadrantScannerPresenter presenter) {
		super(presenter);
		Element eMatrix = getElement();
		final double RELATIVE_WIDTH = 100.0 / Constants.SECTORS_EDGE;
		final double RELATIVE_HEIGHT = 100.0 / Constants.SECTORS_EDGE;
		for (int y = 0; y < Constants.SECTORS_EDGE; y++) {
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				Element eSector = Document.get().createDivElement();
				eSector.setAttribute("id", "quads_" + x + "_" + y);
				eSector.setAttribute("data-uih", "");
				eSector.getStyle().setLeft(RELATIVE_WIDTH * (double) x, Unit.PCT);
				eSector.getStyle().setTop(RELATIVE_HEIGHT * (double) y, Unit.PCT);
				buckets[x][y] = eSector;
				eMatrix.appendChild(eSector);
			}
		}
		bSelectedSector = buckets[0][0];
		eSvgProto = presenter.getApplication().browserAPI.createElementNs("http://www.w3.org/2000/svg", "svg");
		eSvgProto.setAttribute("width", "100%");
		eSvgProto.setAttribute("height", "100%");
		eSvgProto.getStyle().setLeft(0, Unit.PX);
		eSvgProto.getStyle().setTop(0, Unit.PX);
		eSvgProto.getStyle().setProperty("pointerEvents", "none");
		eSvgProto.getStyle().setPosition(Position.ABSOLUTE);
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
	public void drawBeamBetween(int x1, int y1, int x2, int y2, String colour) {
		Element e1 = buckets[x1][y1];
		Element e2 = buckets[x2][y2];

		int x1px = e1.getOffsetLeft() + e1.getClientWidth() / 2;
		int y1px = e1.getOffsetTop() + e1.getClientHeight() / 2;

		int x2px = e2.getOffsetLeft() + e2.getClientWidth() / 2;
		int y2px = e2.getOffsetTop() + e2.getClientHeight() / 2;

		Element eSvg = eSvgProto.cloneNode(false).cast();
		eSvg.setInnerHTML("<line x1='" + x1px + "px' y1='" + y1px + "px' x2='" + x2px + "px' y2='" + y2px
				+ "px' stroke='" + colour + "'/>");
		getElement().appendChild(eSvg);
		beamElements.add(eSvg);
	}

	@Override
	public void clearBeamMarks() {
		beamElements.forEach(e -> e.removeFromParent());
		beamElements.clear();
	}

	@Override
	public void clearSector(int x, int y) {
		Element b = buckets[x][y];
		b.setInnerHTML("");
		b.setClassName("");
	}

	@Override
	public int[] getCoordinatesOfElement(String id) {
		Element e = DOM.getElementById(id);
		return new int[]{e.getOffsetLeft(),e.getOffsetTop()};
	}
	
	

}
