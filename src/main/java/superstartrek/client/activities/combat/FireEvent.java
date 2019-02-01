package superstartrek.client.activities.combat;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Klingon;
import superstartrek.client.model.Thing;

public class FireEvent extends GwtEvent<FireHandler>{
	
	public static Type<FireHandler> TYPE = new Type<FireHandler>();
	public final Thing actor;
	public final Thing target;
	public final String weapon;
	public final int damage;

	public FireEvent(Thing actor, Thing target, String weapon, int damage) {
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
