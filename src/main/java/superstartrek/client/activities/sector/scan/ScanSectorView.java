package superstartrek.client.activities.sector.scan;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import superstartrek.client.activities.PopupView;

public class ScanSectorView extends PopupView<ScanSectorActivity> implements IScanSectorView{

	Element backButton;
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		backButton = getHtmlPanel().getElementById("screen-sectorscan-back");
		DOM.sinkEvents(backButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONMOUSEDOWN | Event.ONTOUCHSTART);
		DOM.setEventListener(backButton, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((ScanSectorPresenter)getPresenter()).doneWithMenu();
			}
		});
		getHtmlPanel().getElement().setId("screen-sectorscan");
	}
	
	public ScanSectorView(ScanSectorPresenter presenter) {
		super(presenter);
	}

	@Override
	public void setObjectName(String value) {
		getHtmlPanel().getElementById("object-name").setInnerText(value);
	}

	@Override
	public void setObjectLocation(String value) {
		getHtmlPanel().getElementById("object-location").setInnerText(value);
	}

	@Override
	public void setObjectQuadrant(String value) {
		getHtmlPanel().getElementById("object-quadrant").setInnerText(value);
	}
	
	@Override
	public void setProperty(String rowId, String cellId, String rowCss, String value) {
		DOM.getElementById(rowId).setClassName(rowCss);
		DOM.getElementById(cellId).setInnerText(value);
	}

	@Override
	protected String getContentForHtmlPanel() {
		return presenter.getApplication().getResources().sectorScanScreen().getText();
	}

	@Override
	public void show() {
		super.show();
		backButton.focus();
	}
}
