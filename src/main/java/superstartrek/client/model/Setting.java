package superstartrek.client.model;

public class Setting {

	protected final double maximum;
	protected final double defaultValue;
	protected double currentUpperBound;
	protected double value;
	protected final String name;
	protected boolean enabled=true;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

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
		return (isEnabled()?1.0:0.0)*getCurrentUpperBound()/getMaximum();
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
		this.value = Math.min(this.value, this.currentUpperBound);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = Math.max(0,Math.min(value, getCurrentUpperBound()));
	}

	public double getMaximum() {
		return maximum;
	}
	
	public double decrease(double delta) {
		value = Math.max(0,value - delta);
		return value;
	}
	
	public void damage(double delta) {
		setCurrentUpperBound(Math.max(0,getCurrentUpperBound()-delta));
		setValue(Math.min(getValue(), getCurrentUpperBound()));
	}
	
	public void reset() {
		setValue(getDefaultValue());
	}

	public void repair() {
		setCurrentUpperBound(getMaximum());
		setEnabled(true);
		reset();
	}
	
	public boolean getBooleanValue() {
		return getValue()>0;
	}
	
	public void setValue(boolean v) {
		setValue(v?1:0);
	}
}
