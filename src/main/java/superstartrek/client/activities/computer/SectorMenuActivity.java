package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseActivity;
import superstartrek.client.activities.Presenter;

public class SectorMenuActivity extends BaseActivity{

	public SectorMenuActivity(Presenter presenter) {
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
				((SectorMenuPresenter)(getPresenter())).onMenuClicked();
			}
		}, ClickEvent.getType());
		hide();
	}
	
	public void setLocation(int x, int y) {
		getElement().getStyle().setTop(y, Unit.PX);
		getElement().getStyle().setLeft(x, Unit.PX);
	}

}
