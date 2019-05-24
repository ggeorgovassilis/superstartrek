package superstartrek.client.control;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Quadrant;

public interface QuadrantActivationHandler extends EventHandler{

	void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant);
}
