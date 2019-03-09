package superstartrek.client.model;

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
}
