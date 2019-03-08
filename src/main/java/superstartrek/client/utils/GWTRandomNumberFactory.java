package superstartrek.client.utils;

import com.google.gwt.user.client.Random;

public class GWTRandomNumberFactory implements RandomNumberFactory{

	@Override
	public double nextDouble() {
		return Random.nextDouble();
	}

}
