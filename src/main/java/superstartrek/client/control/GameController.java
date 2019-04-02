package superstartrek.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;

public class GameController implements GamePhaseHandler, FireHandler, EnterpriseRepairedHandler, ThingMovedHandler, KlingonDestroyedHandler{

	Application application;
	
	public GameController(Application application) {
		this.application = application;
		application.events.addHandler(GameStartedEvent.TYPE, this);
		application.events.addHandler(GameOverEvent.TYPE, this);
		application.events.addHandler(TurnStartedEvent.TYPE, this);
		application.events.addHandler(TurnEndedEvent.TYPE, this);
		application.events.addHandler(KlingonTurnEvent.TYPE, this);
		application.events.addHandler(FireEvent.TYPE, this);
		application.events.addHandler(EnterpriseRepairedEvent.TYPE, this);
		application.events.addHandler(ThingMovedEvent.TYPE, this);
		application.events.addHandler(KlingonDestroyedEvent.TYPE, this);
	}

	@Override
	public void afterFire(FireEvent evt) {
		if (evt.actor == application.starMap.enterprise && !evt.wasAutoFire)
			application.endTurnAfterThis();
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		application.endTurnAfterThis();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == application.starMap.enterprise)
			application.endTurnAfterThis();
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		if (!application.starMap.hasKlingons())
			application.events.fireEvent(new GameOverEvent(GameOverEvent.Outcome.won, "All Klingons were destroyed"));
	}
}
