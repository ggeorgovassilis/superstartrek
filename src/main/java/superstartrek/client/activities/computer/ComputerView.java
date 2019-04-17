package superstartrek.client.activities.computer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
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

public class ComputerView extends BaseScreen<ComputerActivity> implements IComputerView{

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
	protected void decorateScreen() {
		super.decorateScreen();
		addStyleName("computer-screen");
		getElement().setInnerHTML(presenter.getApplication().getResources().computerScreen().getText());
		
		SectorContextMenuPresenter sectorMenuPresenter = new SectorContextMenuPresenter(presenter.getApplication());
		sectorMenuPresenter.setView(new SectorContextMenuView(sectorMenuPresenter));

		QuadrantScannerPresenter quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication(), sectorMenuPresenter);
		HTMLPanel panel = (HTMLPanel)getWidget();
		panel.addAndReplaceElement(new QuadrantScannerView(quadrantScannerPresenter),"quadrantscancontainer");
		
		SRSPresenter srsPresenter = new SRSPresenter(presenter.getApplication());
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
	public void updateShields(int value, int currentUpperBound, int maximum) {
		Element eMax = CSS.querySelectorAll("#cmd_toggleShields .max-indicator").getItem(0);
		Element eValue = CSS.querySelectorAll("#cmd_toggleShields .progress-indicator").getItem(0);
		eMax.getStyle().setWidth(100*currentUpperBound/maximum, Unit.PCT);
		eValue.getStyle().setWidth(100*value/maximum, Unit.PCT);
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
		Element e = ((HTMLPanel)getWidget()).getElementById("quadrant_name");
		e.setInnerText(name);
		e.setClassName(css);
	}

	@Override
	protected Widget createWidgetImplementation() {
		return new HTMLPanel("");
	}
	
}
