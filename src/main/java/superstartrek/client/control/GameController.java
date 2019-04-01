package superstartrek.client.control;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class GameController implements GamePhaseHandler, FireHandler, EnterpriseRepairedHandler, ThingMovedHandler{

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
	}

	@Override
	public void afterFire(FireEvent evt) {
		if (evt.actor == application.starMap.enterprise && !evt.wasAutoFire)
			application.endTurnAfterThis();
	}

	@Override
	public void onEnterpriseRepaired() {
		application.endTurnAfterThis();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == application.starMap.enterprise)
			application.endTurnAfterThis();
	}
}
