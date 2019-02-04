package superstartrek.client.activities.klingons;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Enterprise;

public class KlingonTurnEvent extends GwtEvent<KlingonTurnHandler> {

	public static Type<KlingonTurnHandler> TYPE = new Type<KlingonTurnHandler>();

	@Override
	public Type<KlingonTurnHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(KlingonTurnHandler handler) {
		handler.executeKlingonMove();
	}

}
