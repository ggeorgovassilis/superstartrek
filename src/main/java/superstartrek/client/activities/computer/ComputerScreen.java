package superstartrek.client.activities.computer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSView;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuView;

public class ComputerScreen extends BaseScreen<ComputerPresenter> implements IComputerScreen, ClickHandler {

	Element eSkip;
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

	@Override
	public void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos) {
		final String sf = "status-flag ";
		eStatusIconImpulse.setClassName(sf+cssImpulse);
		eStatusIconTactical.setClassName(sf+cssTactical);
		eStatusIconPhasers.setClassName(sf+cssPhasers);
		eStatusIconTorpedos.setClassName(sf+cssTorpedos);
	}

	@Override
	protected void decorateScreen() {
		super.decorateScreen();
		addStyleName("computer-screen");
		getElement().setInnerHTML(presenter.getApplication().getResources().computerScreen().getText());

		SectorContextMenuPresenter sectorMenuPresenter = new SectorContextMenuPresenter(presenter.getApplication());
		sectorMenuPresenter.setView(new SectorContextMenuView(sectorMenuPresenter));

		QuadrantScannerPresenter quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication(),
				sectorMenuPresenter);
		HTMLPanel panel = getWidgetAs();
		panel.addAndReplaceElement(new QuadrantScannerView(quadrantScannerPresenter), "quadrantscancontainer");

		SRSPresenter srsPresenter = new SRSPresenter(presenter.getApplication());
		SRSView srsView = new SRSView(srsPresenter);
		panel.addAndReplaceElement(srsView, "shortrangescan");
		eStatusIconImpulse = panel.getElementById("short-status-impulse");
		eStatusIconTactical = panel.getElementById("short-status-tactical-computer");
		eStatusIconTorpedos = panel.getElementById("short-status-torpedo-bay");
		eStatusIconPhasers = panel.getElementById("short-status-phasers");
		eMaxAntimatter = CSS.querySelectorAll("#cmd_showStatusReport .progress-indicator").getItem(0);
		eMaxShields = CSS.querySelectorAll("#cmd_toggleShields .max-indicator").getItem(0);
		eValueShields = CSS.querySelectorAll("#cmd_toggleShields .progress-indicator").getItem(0);
		eQuadrantName = ((HTMLPanel) getWidget()).getElementById("quadrant_name");
		eSkip = panel.getElementById("cmd_skip");
		eStarDate = DOM.getElementById("stardate");
		eScore = DOM.getElementById("score");
		eLrsButton = DOM.getElementById("lrs-button");
		eAboveRadarSlot = DOM.getElementById("above-radar-slot");
		addHandler(this, ClickEvent.getType());
		DOM.sinkEvents(panel.getElement(), Event.ONCLICK);
	}

	@Override
	public void updateShields(double value, double currentUpperBound, double maximum) {
		eMaxShields.getStyle().setWidth(100.0 * currentUpperBound / maximum, Unit.PCT);
		eValueShields.getStyle().setWidth(100.0 * value / maximum, Unit.PCT);
	}

	public ComputerScreen(ComputerPresenter presenter) {
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
	protected Widget createWidgetImplementation() {
		return new HTMLPanel("");
	}

	@Override
	public void updateAntimatter(double value, double maximum) {
		eMaxAntimatter.getStyle().setWidth(100.0 * value / maximum, Unit.PCT);
	}

	@Override
	public void onClick(ClickEvent event) {
		Element target = event.getNativeEvent().getEventTarget().cast();
		if (eSkip.isOrHasChild(target))
			presenter.onSkipButtonClicked();
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
		DOM.getElementById(rowId).setClassName(rowCss);
		Element e = DOM.getElementById(cellId);
		if (e!=null) e.setInnerText(value);
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

}
