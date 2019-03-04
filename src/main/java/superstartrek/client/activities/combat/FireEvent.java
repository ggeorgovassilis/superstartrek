package superstartrek.client.activities.combat;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class FireEvent extends GwtEvent<FireHandler>{
	
	public static Type<FireHandler> TYPE = new Type<FireHandler>();
	public final Vessel actor;
	public final Thing target;
	public final String weapon;
	public final double damage;

	public FireEvent(Vessel actor, Thing target, String weapon, double damage) {
		this.actor = actor;
		this.target= target;
		this.weapon = weapon;
		this.damage = damage;
	}
	
	@Override
	public Type<FireHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FireHandler handler) {
		handler.onFire(actor, target, weapon, damage);
	}

}
