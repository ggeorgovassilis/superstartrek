package superstartrek.client.activities.computer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSView;
import superstartrek.client.activities.glasspanel.GlassPanelPresenter;

public class ComputerView extends BaseScreen<ComputerActivity>{

	QuadrantScannerPresenter quadrantScannerPresenter;
	QuadrantScannerView quadrantScannerActivity;
	SRSPresenter srsPresenter;
	
	Element eDockInStarbase;
	
	@Override
	protected void setupUI() {
		super.setupUI();
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
		DOM.setEventListener(getElement(), new EventListener() {
			@Override
			public void onBrowserEvent(Event event) {
				((ComputerPresenter)getPresenter()).onDockInStarbaseButtonClicked();
			}
		});
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
}
