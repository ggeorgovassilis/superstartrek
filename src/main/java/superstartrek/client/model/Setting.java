package superstartrek.client.model;

public class Setting {

	protected final double maximum;
	protected final double defaultValue;
	protected double currentUpperBound;
	protected double value;
	protected String name;
	
	public Setting(double defaultValue, double maximum) {
		this.maximum = maximum;
		this.defaultValue = defaultValue;
		this.currentUpperBound = maximum;
		this.value = defaultValue;
	}
	
	public double getDefaultValue() {
		return defaultValue;
	}

	public String percentage() {
		return "%"+Math.floor(100*value/maximum);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		this.value = value;
	}

	public double getMaximum() {
		return maximum;
	}

}
