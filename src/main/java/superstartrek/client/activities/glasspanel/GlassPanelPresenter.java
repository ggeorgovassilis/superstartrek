package superstartrek.client.activities.glasspanel;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;

public class GlassPanelPresenter extends BasePresenter implements GlassPanelHandler, GameStartedHandler{

	public GlassPanelPresenter(Application application) {
		super(application);
		application.events.addHandler(GlassPanelEvent.TYPE, this);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}
	
	public void glassPanelClicked(){
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
		((GlassPanelActivity)getScreen()).hide();
	}

	public void glassPanelWasClicked(){
		application.events.fireEvent(new GlassPanelEvent(Action.click));
	}

	@Override
	public void glassPanelShown() {
		((GlassPanelActivity)getScreen()).show();
	}

	@Override
	public void glassPanelHidden() {
		((GlassPanelActivity)getScreen()).hide();
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		((GlassPanelActivity)getScreen()).hide();
	}

}
