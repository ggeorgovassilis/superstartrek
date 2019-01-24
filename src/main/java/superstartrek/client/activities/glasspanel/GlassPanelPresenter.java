package superstartrek.client.activities.glasspanel;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;

public class GlassPanelPresenter extends BasePresenter<GlassPanelActivity> implements GlassPanelHandler, GameStartedHandler{

	public GlassPanelPresenter(Application application) {
		super(application);
		application.events.addHandler(GlassPanelEvent.TYPE, this);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}
	
	@Override
	public void glassPanelClicked(){
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
		getView().hide();
	}

	public void glassPanelWasClicked(){
		application.events.fireEvent(new GlassPanelEvent(Action.click));
	}

	@Override
	public void glassPanelShown() {
		getView().show();
	}

	@Override
	public void glassPanelHidden() {
		getView().hide();
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		getView().hide();
	}

}
