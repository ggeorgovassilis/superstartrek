package superstartrek.client.activities.sector.scan;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import superstartrek.client.activities.PopupView;
import superstartrek.client.utils.HtmlWidget;

public class ScanSectorView extends PopupView<ScanSectorPresenter> implements IScanSectorView{

	Element backButton;
	
	@Override
	public void decorateWidget() {
		super.decorateWidget();
		HtmlWidget panel = getWidgetAs();
		backButton = panel.getElementById("screen-sectorscan-back");
		DOM.sinkEvents(backButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONMOUSEDOWN | Event.ONTOUCHSTART);
		DOM.setEventListener(backButton, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				presenter.doneWithMenu();
			}
		});
		panel.getElement().setId("screen-sectorscan");
	}
	
	public ScanSectorView(ScanSectorPresenter presenter) {
		super(presenter);
	}

	@Override
	public void setObjectName(String value) {
		((HtmlWidget)getWidget()).getElementById("object-name").setInnerText(value);
	}

	@Override
	public void setObjectLocation(String value) {
		((HtmlWidget)getWidget()).getElementById("object-location").setInnerText(value);
	}

	@Override
	public void setObjectQuadrant(String value) {
		((HtmlWidget)getWidget()).getElementById("object-quadrant").setInnerText(value);
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
