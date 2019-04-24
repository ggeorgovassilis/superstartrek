package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import superstartrek.client.model.Thing;

public interface EnergyConsumptionHandler extends EventHandler{

	public static class EnergyConsumptionEvent extends GwtEvent<EnergyConsumptionHandler>{

		final Thing consumer;
		final double value;
		final String type;
		
		public EnergyConsumptionEvent(Thing consumer, double value, String type){
			this.consumer = consumer;
			this.value = value;
			this.type = type;
		}
		public static Type<EnergyConsumptionHandler> TYPE = new Type<EnergyConsumptionHandler>();
		
		@Override
		public Type<EnergyConsumptionHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(EnergyConsumptionHandler handler) {
			handler.handleEnergyConsumption(consumer, value, type);
		}

	}
	
	default void handleEnergyConsumption(Thing consumer, double value, String type) {};

}
