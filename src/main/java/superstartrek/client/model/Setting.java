package superstartrek.client.model;

public class Setting {

	protected final double maximum;
	protected double currentUpperBound;
	protected double value;
	protected final String name;
	protected boolean enabled=true;
	
	public Setting(String name, double maximum) {
		this.name = name;
		this.maximum = maximum;
		this.currentUpperBound = maximum;
		this.value = maximum;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	
	public void disable() {
		setCurrentUpperBound(0);
		setValue(0);
	}
	
	public void reset() {
		setValue(getMaximum());
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
	
	@Override
	public String toString() {
		return getName()+" : "+getValue()+"/"+getCurrentUpperBound()+"/"+getMaximum();
	}
}
