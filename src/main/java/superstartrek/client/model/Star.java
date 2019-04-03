package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;

public class Star extends Thing implements FireHandler {

	public enum StarClass {

		O("\u2605", "Class O", "star-class-o"), B("\u2606", "Class B", "star-class-b"),
		A("\u2739", "Class A", "star-class-a"), F("\u2739","Class F", "star-class-f");

		StarClass(String symbol, String typeName, String css) {
			this.symbol = symbol;
			this.typeName = typeName;
			this.css = css;
		}

		public final String symbol;
		public final String typeName;
		public final String css;
	}

	public Star(int x, int y, boolean registerEvents, StarClass sc) {
		setName(sc.typeName + " star");
		setSymbol(sc.symbol);
		setCss("star " + sc.css);
		if (registerEvents)
			Application.get().events.addHandler(FireEvent.TYPE, this);
		setLocation(Location.location(x, y));
	}

	public Star(int x, int y, StarClass sc) {
		this(x, y, true, sc);
	}

	@Override
	public void onFire(FireEvent evt) {
		if (evt.target == this)
			Application.get().message(evt.weapon + " hit " + getName() + " at " + getLocation());
	}

}
