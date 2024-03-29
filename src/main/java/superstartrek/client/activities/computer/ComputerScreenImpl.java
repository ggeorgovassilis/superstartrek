package superstartrek.client.activities.computer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerViewImpl;
import superstartrek.client.activities.computer.sectorcontextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.computer.sectorcontextmenu.SectorContextMenuViewImpl;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSViewImpl;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;

public class ComputerScreenImpl extends BaseScreen<ComputerPresenter> implements ComputerScreen{

	Element eStatusIconImpulse;
	Element eStatusIconTactical;
	Element eStatusIconPhasers;
	Element eStatusIconTorpedos;
	Element eStarDate;
	Element eScore;
	Element eLrsButton;
	Element eMaxAntimatter;
	Element eMaxShields;
	Element eValueShields;
	Element eQuadrantName;
	Element eAboveRadarSlot;
	Element eToggleShields;

	@Override
	public void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos) {
		final String sf = "status-flag ";
		eStatusIconImpulse.setClassName(sf+cssImpulse);
		eStatusIconTactical.setClassName(sf+cssTactical);
		eStatusIconPhasers.setClassName(sf+cssPhasers);
		eStatusIconTorpedos.setClassName(sf+cssTorpedos);
	}

	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		addStyleName("computer-screen");
		getElement().setInnerHTML(templates.computerScreen().getText());

		SectorContextMenuPresenter sectorMenuPresenter = new SectorContextMenuPresenter();
		sectorMenuPresenter.setView(new SectorContextMenuViewImpl(sectorMenuPresenter));

		QuadrantScannerPresenter quadrantScannerPresenter = new QuadrantScannerPresenter(sectorMenuPresenter);
		new QuadrantScannerViewImpl(quadrantScannerPresenter).replaceElementWithThis("quadrantscancontainer");

		SRSPresenter srsPresenter = new SRSPresenter();
		SRSViewImpl srsView = new SRSViewImpl(srsPresenter);
		srsView.replaceElementWithThis("shortrangescan");
		eStatusIconImpulse = getElementById("short-status-impulse");
		eStatusIconTactical = getElementById("short-status-tactical-computer");
		eStatusIconTorpedos = getElementById("short-status-torpedo-bay");
		eStatusIconPhasers = getElementById("short-status-phasers");
		eMaxAntimatter = CSS.querySelectorAll("#cmd_showStatusReport .progress-indicator").getItem(0);
		eMaxShields = CSS.querySelectorAll("#cmd_toggleShields .max-indicator").getItem(0);
		eValueShields = CSS.querySelectorAll("#cmd_toggleShields .progress-indicator").getItem(0);
		eQuadrantName = getElementById("quadrant_name");
		eStarDate = getElementById("stardate");
		eScore = getElementById("score");
		eLrsButton = getElementById("lrs-button");
		eAboveRadarSlot = getElementById("above-radar-slot");
		eToggleShields = getElementById("cmd_toggleShields");
	}

	@Override
	public void updateShields(double value, double currentUpperBound, double maximum) {
		eMaxShields.getStyle().setWidth(100.0 * currentUpperBound / maximum, Unit.PCT);
		eValueShields.getStyle().setWidth(100.0 * value / maximum, Unit.PCT);
	}

	public ComputerScreenImpl(ComputerPresenter presenter) {
		super(presenter);
	}

	@Override
	public void showStarDate(String sd) {
		eStarDate.setInnerText(sd);
	}

	@Override
	public void setQuadrantName(String name, String css) {
		eQuadrantName.setInnerText(name);
		eQuadrantName.setClassName(css);
	}

	@Override
	public void updateAntimatter(double value, double maximum) {
		eMaxAntimatter.getStyle().setWidth(100.0 * value / maximum, Unit.PCT);
	}

	@Override
	public void enableLlrsButton() {
		CSS.setEnabled(eLrsButton, true);
		eLrsButton.setAttribute("href", "#longrangescan");
	}

	@Override
	public void disableLrsButton() {
		CSS.setEnabled(eLrsButton, false);
		eLrsButton.setAttribute("href", "#computer");
	}

	@Override
	public void showScore(String score) {
		eScore.setInnerText(score);
	}

	@Override
	public void addAntimatterCss(String css) {
		eMaxAntimatter.addClassName(css);
	}

	@Override
	public void removeAntimatterCss(String css) {
		eMaxAntimatter.removeClassName(css);
	}

	@Override
	public void setCommandBarMode(String mode) {
		eAboveRadarSlot.setClassName(mode);
	}

	@Override
	public void setScanProperty(String rowId, String cellId, String rowCss, String value) {
		getElementById(rowId).setClassName(rowCss);
		Element e = getElementById(cellId);
		if (e!=null) e.setInnerText(value);
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

	@Override
	public void removeShieldCss(String css) {
		eToggleShields.removeClassName(css);
	}
	
	@Override
	public void addShieldCss(String css) {
		eToggleShields.addClassName(css);
	}

	@Override
	public void updateTorpedoLabel(String value) {
		eStatusIconTorpedos.setInnerText(value);
	}

}
