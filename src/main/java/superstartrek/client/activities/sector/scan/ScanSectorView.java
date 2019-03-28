package superstartrek.client.activities.sector.scan;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;

public class ScanSectorView extends BaseScreen<ScanSectorActivity> implements IScanSectorView{

	HTMLPanel html;
	PopupPanel popup;
	
	@Override
	public void show() {
		popup.show();
	}
	
	@Override
	public void hide() {
		popup.hide();
	}
	
	@Override
	protected Widget createWidgetImplementation() {
		popup = new PopupPanel(true, true);
		popup.setGlassStyleName("glasspanel");
		html = new HTMLPanel(presenter.getApplication().getResources().sectorScanScreen().getText());
		popup.add(html);
		Element button = html.getElementById("screen-sectorscan-back");
		DOM.sinkEvents(button, Event.ONCLICK);
		DOM.setEventListener(button, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((ScanSectorPresenter)getPresenter()).doneWithMenu();
			}
		});

		popup.setGlassEnabled(true);
		return new FlowPanel();
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		getPresenter().getApplication().page.add(this);
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Element e = event.getNativeEvent().getEventTarget().cast();
				String command = e.getAttribute("id");
				if (command!=null && !command.isEmpty())
					((ScanSectorPresenter)getPresenter()).onCommandClicked(command);
				
			}
		}, ClickEvent.getType());
	}
	
	public ScanSectorView(ScanSectorPresenter presenter) {
		super(presenter);
	}

	@Override
	public void setObjectName(String value) {
		html.getElementById("object-name").setInnerText(value);
	}

	@Override
	public void setObjectLocation(String value) {
		html.getElementById("object-location").setInnerText(value);
	}

	@Override
	public void setObjectQuadrant(String value) {
		html.getElementById("object-quadrant").setInnerText(value);
	}
	
	@Override
	public void setProperty(String rowId, String cellId, String rowCss, String value) {
		DOM.getElementById(rowId).setClassName(rowCss);
		DOM.getElementById(cellId).setInnerText(value);
	}


}
