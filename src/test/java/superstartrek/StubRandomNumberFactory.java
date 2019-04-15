package superstartrek;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.utils.RandomNumberFactory;

public class StubRandomNumberFactory implements RandomNumberFactory{

	List<Double> d;
	List<Integer> i;
	
	public StubRandomNumberFactory(double[] doubles, int[] ints) {
		d = new ArrayList<>();
		for (double x:doubles)
			d.add(x);
		i = new ArrayList<>();
		for (int x:ints)
			i.add(x);
	}
	
	@Override
	public double nextDouble() {
		return d.remove(0);
	}

	@Override
	public int nextInt(int upperBound) {
		return i.remove(0);
	}

}
