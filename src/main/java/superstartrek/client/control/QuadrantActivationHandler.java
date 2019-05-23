package superstartrek.client.control;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Quadrant;

public interface QuadrantActivationHandler extends BaseHandler{

	void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant);
}
