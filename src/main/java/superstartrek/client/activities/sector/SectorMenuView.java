package superstartrek.client.activities.sector;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseView;

public class SectorMenuView extends BaseView<SectorMenuActivity>{

	public SectorMenuView(SectorMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	protected HTMLPanel createPanel() {
		return new HTMLPanel(Resources.INSTANCE.sectorSelectionMenu().getText());
	}
	
	@Override
	public void finishUiConstruction() {
		addStyleName("sectorselectionbar");
		presenter.getApplication().page.add(this);
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Element e = event.getNativeEvent().getEventTarget().cast();
				String command = e.getAttribute("id");
				if ("cmd_computer".equals(command))
					((SectorMenuPresenter)getPresenter()).onComputerClicked();
				
			}
		}, ClickEvent.getType());
		hide();
	}
	
	public void setLocation(int x, int y) {
		getElement().getStyle().setTop(y, Unit.PX);
		getElement().getStyle().setLeft(x, Unit.PX);
	}

}
