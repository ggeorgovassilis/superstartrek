package superstartrek.client.activities.computer;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSView;
import superstartrek.client.activities.glasspanel.GlassPanelPresenter;
import superstartrek.client.model.Setting;

public class ComputerView extends BaseScreen<ComputerActivity>{

	QuadrantScannerPresenter quadrantScannerPresenter;
	QuadrantScannerView quadrantScannerActivity;
	SRSPresenter srsPresenter;
	
	Element eDockInStarbase;
	Element eSkip;
	Element eToggleShields;
	Element eRepair;
	
	@Override
	protected void setupCompositeUI() {
		super.setupCompositeUI();
		getElement().setInnerHTML(Resources.INSTANCE.computerScreen().getText());
		presenter.getApplication().page.add(this);
		quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication());
		quadrantScannerActivity = new QuadrantScannerView(quadrantScannerPresenter);
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

		eSkip = DOM.getElementById("cmd_skip");
		DOM.sinkEvents(eSkip, Event.ONCLICK);
		DOM.setEventListener(eSkip, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((ComputerPresenter)getPresenter()).onSkipButtonClicked();
			}
		});
		
		eToggleShields = DOM.getElementById("cmd_toggleShields");
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
		
		setRepairButtonVisibility(false);
	}
	
	public void updateShields(Setting shields) {
		Element eMax = CSS.querySelectorAll("#cmd_toggleShields .max-indicator").getItem(0);
		Element eValue = CSS.querySelectorAll("#cmd_toggleShields .progress-indicator").getItem(0);
		eMax.getStyle().setWidth(100*shields.getCurrentUpperBound()/shields.getMaximum(), Unit.PCT);
		eValue.getStyle().setWidth(100*shields.getValue()/shields.getCurrentUpperBound(), Unit.PCT);
	}
	
	public ComputerView(ComputerPresenter presenter) {
		super(presenter);
	}
	
	public void showStarDate(String sd){
		DOM.getElementById("stardate").setInnerText(sd);
	}
	
	public void setDockInStarbaseButtonVisibility(boolean visible) {
		eDockInStarbase.getStyle().setDisplay(visible?Display.INITIAL:Display.NONE);
	}
	
	public void setRepairButtonVisibility(boolean visible) {
		eRepair.getStyle().setDisplay(visible?Display.INITIAL:Display.NONE);
	}
}
