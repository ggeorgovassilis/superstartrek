package superstartrek.client.activities.computer;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Thing;

public interface EnergyConsumptionHandler extends BaseHandler{

	default void handleEnergyConsumption(Thing consumer, double value, String type) {};

}
