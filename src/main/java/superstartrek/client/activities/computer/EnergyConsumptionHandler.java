package superstartrek.client.activities.computer;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.Thing;

public interface EnergyConsumptionHandler extends EventHandler{

	default void handleEnergyConsumption(Thing consumer, double value, String type) {};

}
