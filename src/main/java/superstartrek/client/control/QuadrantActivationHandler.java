package superstartrek.client.control;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.Quadrant;

public interface QuadrantActivationHandler extends EventHandler{

	void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant);
}
