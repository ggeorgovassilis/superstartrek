package superstartrek.client.space;

public class StarBase extends Thing{

	public StarBase() {
		setName("a Federation star base");
		setSymbol("&lt;!&gt;");
		setCss("starbase");
	}
	
	public StarBase(Location l) {
		this();
		setLocation(l);
	}
	
	public static boolean is(Thing thing) {
		return thing instanceof StarBase;
	}
}
