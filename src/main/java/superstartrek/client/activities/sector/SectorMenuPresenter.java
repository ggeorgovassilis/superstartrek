package superstartrek.client.activities.sector;

import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.glasspanel.GlassPanelHandler;

public class SectorMenuPresenter extends BasePresenter<SectorMenuActivity> implements SectorSelectedHandler, GlassPanelHandler{

	public SectorMenuPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(GlassPanelEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		((SectorMenuView)getView()).setLocation(0, event.screenY);
		getView().show();
		getApplication().events.fireEvent(new GlassPanelEvent(GlassPanelEvent.Action.show));
	}
	
	protected void hideMenu() {
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
		getView().hide();
	}
	
	public void onMenuClicked() {
		hideMenu();
	}

	@Override
	public void glassPanelShown() {
	}

	@Override
	public void glassPanelHidden() {
		getView().hide();
	}

	@Override
	public void glassPanelClicked() {
	}
	
	public void onComputerClicked() {
		hideMenu();
	}

}
