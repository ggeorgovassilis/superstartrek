package superstartrek.client.utils;

import com.google.gwt.core.shared.GWT;

public class Random {
	
	protected static RandomNumberFactory factory = new GWTRandomNumberFactory();
	
	public static void setFactory(RandomNumberFactory f) {
		factory = f;
	}

	public static double nextDouble() {
		if (GWT.isClient())
			return com.google.gwt.user.client.Random.nextDouble();
		else return Math.random();
	}
}
