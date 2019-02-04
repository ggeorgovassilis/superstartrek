package superstartrek.client.activities.sector.scan;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;

public class ScanSectorView extends BaseScreen<ScanSectorActivity>{

	@Override
	protected HTMLPanel createPanel() {
		HTMLPanel panel = new HTMLPanel(Resources.INSTANCE.sectorScanScreen().getText());
		return panel;
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

	public void setObjectName(String value) {
		panel.getElementById("object-name").setInnerText(value);
	}

	public void setObjectLocation(String value) {
		panel.getElementById("object-location").setInnerText(value);
	}

	public void setObjectQuadrant(String value) {
		panel.getElementById("object-quadrant").setInnerText(value);
	}

}