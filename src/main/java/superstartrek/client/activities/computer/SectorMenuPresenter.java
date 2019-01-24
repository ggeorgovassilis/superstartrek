package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.glasspanel.GlassPanelHandler;

public class SectorMenuPresenter extends BasePresenter implements SectorSelectedHandler, GlassPanelHandler{

	public SectorMenuPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(GlassPanelEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		((SectorMenuActivity)getScreen()).setLocation(0, event.screenY);
		((SectorMenuActivity)getScreen()).show();
		getApplication().events.fireEvent(new GlassPanelEvent(GlassPanelEvent.Action.show));
	}
	
	public void onMenuClicked() {
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
		((SectorMenuActivity)getScreen()).hide();
	}

	@Override
	public void glassPanelShown() {
	}

	@Override
	public void glassPanelHidden() {
		((SectorMenuActivity)getScreen()).hide();
	}

	@Override
	public void glassPanelClicked() {
	}

}
