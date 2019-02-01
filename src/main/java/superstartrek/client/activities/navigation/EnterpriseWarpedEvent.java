package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.GwtEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public class EnterpriseWarpedEvent extends GwtEvent<EnterpriseWarpedHandler>{

	public final static Type<EnterpriseWarpedHandler> TYPE = new Type<EnterpriseWarpedHandler>();
	
	public final Enterprise enterprise;
	public final Quadrant qFrom;
	public final Location lFrom;
	public final Location lTo;
	public final Quadrant qTo;
	
	public EnterpriseWarpedEvent(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		this.enterprise = enterprise;
		this.qFrom = qFrom;
		this.qTo = qTo;
		this.lFrom = lFrom;
		this.lTo = lTo;
	}
	
	@Override
	public Type<EnterpriseWarpedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EnterpriseWarpedHandler handler) {
		handler.onEnterpriseWarped(enterprise, qFrom, lFrom, qTo, lTo);
	}

}
