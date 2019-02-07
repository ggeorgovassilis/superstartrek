package superstartrek.client.model;

public class Setting {

	protected final double maximum;
	protected final double defaultValue;
	protected double currentUpperBound;
	protected double value;
	protected final String name;
	
	public Setting(String name, double defaultValue, double maximum) {
		this.name = name;
		this.maximum = maximum;
		this.defaultValue = defaultValue;
		this.currentUpperBound = maximum;
		this.value = defaultValue;
	}
	
	public double getDefaultValue() {
		return defaultValue;
	}

	public double ratio() {
		return value/maximum;
	}

	public double health() {
		return getCurrentUpperBound()/getMaximum();
	}

	public int percentageHealth() {
		return (int)Math.floor(100*getCurrentUpperBound()/getMaximum());
	}

	public int percentage() {
		return (int)Math.floor(100*ratio());
	}

	public String getName() {
		return name;
	}

	public double getCurrentUpperBound() {
		return currentUpperBound;
	}

	public void setCurrentUpperBound(double currentUpperBound) {
		this.currentUpperBound = currentUpperBound;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = Math.min(value, getCurrentUpperBound());
	}

	public double getMaximum() {
		return maximum;
	}
	
	public double decrease(double delta) {
		value = Math.max(0,value - delta);
		return value;
	}
	
	public void reset() {
		setValue(getDefaultValue());
	}

	public void repair() {
		setCurrentUpperBound(getMaximum());
		reset();
	}
}
