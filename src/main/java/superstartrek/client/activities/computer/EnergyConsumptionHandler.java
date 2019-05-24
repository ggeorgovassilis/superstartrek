package superstartrek.client.activities.computer;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Thing;

public interface EnergyConsumptionHandler extends EventHandler{

	default void handleEnergyConsumption(Thing consumer, double value, String type) {};

}
