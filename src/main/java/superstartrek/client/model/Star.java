package superstartrek.client.model;

public class Star extends Thing{

	public enum StarClass {

		O("\u2605", "Class O", "star-class-o"), B("\u2606", "Class B", "star-class-b"),
		A("\u2606", "Class A", "star-class-a"), F("\u2605","Class F", "star-class-f");

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
		setLocation(Location.location(x, y));
	}

	public Star(int x, int y, StarClass sc) {
		this(x, y, true, sc);
	}
	
	public static boolean is(Thing thing) {
		return thing instanceof Star;
	}

}
