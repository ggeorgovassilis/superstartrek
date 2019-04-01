package superstartrek.client.activities.computer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSView;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorMenuView;
import superstartrek.client.model.Setting;

public class ComputerView extends BaseScreen<ComputerActivity> implements IComputerView{

	QuadrantScannerPresenter quadrantScannerPresenter;
	QuadrantScannerView quadrantScannerActivity;
	SRSPresenter srsPresenter;
	
	Element eDockInStarbase;
	Element eRepair;
	Element eStatusIconImpulse;
	Element eStatusIconTactical;
	Element eStatusIconPhasers;
	Element eStatusIconTorpedos;
	
	@Override
	public void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos) {
		eStatusIconImpulse.setClassName(cssImpulse);
		eStatusIconTactical.setClassName(cssTactical);
		eStatusIconPhasers.setClassName(cssPhasers);
		eStatusIconTorpedos.setClassName(cssTorpedos);
	}
	
	@Override
	protected void setupCompositeUI() {
		super.setupCompositeUI();
		getElement().setInnerHTML(presenter.getApplication().getResources().computerScreen().getText());
		presenter.getApplication().page.add(this);
		
		SectorMenuPresenter sectorMenuPresenter = new SectorMenuPresenter(presenter.getApplication());
		sectorMenuPresenter.setView(new SectorMenuView(sectorMenuPresenter));

		quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication(), sectorMenuPresenter);
		quadrantScannerActivity = new QuadrantScannerView(quadrantScannerPresenter);
		HTMLPanel panel = (HTMLPanel)getWidgetImplementation();
		panel.add(quadrantScannerActivity,"quadrantscancontainer");
		
		srsPresenter = new SRSPresenter(presenter.getApplication());
		SRSView srsView = new SRSView(srsPresenter);
		panel.addAndReplaceElement(srsView, "shortrangescan");
		eDockInStarbase = DOM.getElementById("cmd_dockInStarbase");
		DOM.sinkEvents(eDockInStarbase, Event.ONCLICK);
		DOM.setEventListener(eDockInStarbase, new EventListener() {
			@Override
			public void onBrowserEvent(Event event) {
				((ComputerPresenter)getPresenter()).onDockInStarbaseButtonClicked();
			}
		});

		Element eSkip = DOM.getElementById("cmd_skip");
		DOM.sinkEvents(eSkip, Event.ONCLICK);
		DOM.setEventListener(eSkip, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((ComputerPresenter)getPresenter()).onSkipButtonClicked();
			}
		});
		
		Element eToggleShields = DOM.getElementById("cmd_toggleShields");
		DOM.sinkEvents(eToggleShields, Event.ONCLICK);
		DOM.setEventListener(eToggleShields, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((ComputerPresenter)getPresenter()).onToggleShieldsButtonClicked();
			}
		});
		
		eRepair = DOM.getElementById("cmd_repairProvisionally");
		DOM.sinkEvents(eRepair, Event.ONCLICK);
		DOM.setEventListener(eRepair, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((ComputerPresenter)getPresenter()).onRepairButtonClicked();
			}
		});
		
		eStatusIconImpulse = CSS.querySelectorAll("#cmd_showStatusReport .impulse").getItem(0);
		eStatusIconTactical = CSS.querySelectorAll("#cmd_showStatusReport .tactical-computer").getItem(0);
		eStatusIconTorpedos = CSS.querySelectorAll("#cmd_showStatusReport .torpedo-bay").getItem(0);
		eStatusIconPhasers = CSS.querySelectorAll("#cmd_showStatusReport .phasers").getItem(0);
		setRepairButtonVisibility(false);
	}
	
	@Override
	public void updateShields(Setting shields) {
		Element eMax = CSS.querySelectorAll("#cmd_toggleShields .max-indicator").getItem(0);
		Element eValue = CSS.querySelectorAll("#cmd_toggleShields .progress-indicator").getItem(0);
		eMax.getStyle().setWidth(100*shields.getCurrentUpperBound()/shields.getMaximum(), Unit.PCT);
		eValue.getStyle().setWidth(100*shields.getValue()/shields.getMaximum(), Unit.PCT);
	}
	
	public ComputerView(ComputerPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public void showStarDate(String sd){
		DOM.getElementById("stardate").setInnerText(sd);
	}
	
	@Override
	public void setDockInStarbaseButtonVisibility(boolean visible) {
		eDockInStarbase.getStyle().setDisplay(visible?Display.INITIAL:Display.NONE);
	}
	
	@Override
	public void setRepairButtonVisibility(boolean visible) {
		eRepair.getStyle().setDisplay(visible?Display.INITIAL:Display.NONE);
	}

	@Override
	public void setQuadrantName(String name, String css) {
		Element e = ((HTMLPanel)widgetImpl).getElementById("quadrant_name");
		e.setInnerText(name);
		e.setClassName(css);
	}
	
}
