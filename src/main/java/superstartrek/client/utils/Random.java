package superstartrek.client.utils;

public class Random {
	
	protected RandomNumberFactory factory;
	
	public Random() {
	}
	
	public Random(RandomNumberFactory factory) {
		this.factory = factory;
	}
	
	public void setFactory(RandomNumberFactory f) {
		factory = f;
	}

	public double nextDouble() {
		return factory.nextDouble();
	}

	public int nextInt(int upperBound) {
		return factory.nextInt(upperBound);
	}

}
