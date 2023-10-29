package superstartrek.client.space;

public class StarBase extends Thing {

	public StarBase() {
	}

	@Override
	public String getName() {
		return "a Federation star base";
	}

	@Override
	public String getSymbol() {
		return "&lt;!&gt;";
	}

	@Override
	public String getCss() {
		return "starbase";
	}

	public StarBase(Location l) {
		this();
		setLocation(l);
	}

	public static boolean is(Thing thing) {
		return thing instanceof StarBase;
	}
}
