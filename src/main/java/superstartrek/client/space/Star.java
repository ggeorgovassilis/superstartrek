package superstartrek.client.space;

public class Star extends Thing{

	public static enum StarClass {

		O("\u2605", "Class O star", "star-class-o",0.1), 
		B("\u2606", "Class B star", "star-class-b",0.1),
		A("\u2606", "Class A star", "star-class-a",0.1), 
		F("\u2605","Class F star", "star-class-f",0.1),
		Asteroid("*","Asteroid","star-class-asteroid",1.0);

		StarClass(String symbol, String typeName, String css, double probability) {
			this.symbol = symbol;
			this.typeName = typeName;
			this.css = css;
			this.probability = probability;
		}

		public final String symbol;
		public final String typeName;
		public final String css;
		public final double probability;
	}
	
	final StarClass sc;

	public Star(Location location, StarClass sc) {
		this.sc = sc;
		setLocation(location);
	}
	
	public static boolean is(Thing thing) {
		return thing instanceof Star;
	}

	@Override
	public String getSymbol() {
		return sc.symbol;
	}

	@Override
	public String getName() {
		return sc.typeName;
	}
	
	@Override
	public String getCss() {
		return "star "+sc.css;
	}
	
	public StarClass getStarClass() {
		return sc;
	}

}
