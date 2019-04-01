package superstartrek.client.activities.combat;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class FireEvent extends GwtEvent<FireHandler>{
	
	public enum Phase{fire,afterFire};
	public static Type<FireHandler> TYPE = new Type<FireHandler>();
	public final Vessel actor;
	public final Thing target;
	public final String weapon;
	public final double damage;
	public final Phase phase;
	public final boolean wasAutoFire;
	
	public FireEvent(Phase phase, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
		this.actor = actor;
		this.target= target;
		this.weapon = weapon;
		this.damage = damage;
		this.phase = phase;
		this.wasAutoFire = wasAutoFire;
	}
	
	@Override
	public Type<FireHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FireHandler handler) {
		if (phase == Phase.fire)
			handler.onFire(this);
		else if (phase == Phase.afterFire)
			handler.afterFire(this);
	}

}
