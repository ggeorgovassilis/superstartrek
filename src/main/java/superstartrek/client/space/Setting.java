package superstartrek.client.space;

/**
 * Settings model numerical or boolean attributes of vessels such as reactor output or the number of torpedos.
 */
public class Setting {

	// Largest value the setting can have
	double maximum;
	
	// Temporary largest value the setting can have. This is used to implement the effect damage has on the setting
	// eg. reduced reactor output as the result of receiving damage.
	double currentUpperBound;
	
	// current setting value. Has to be in the range of 0 ≤ value ≤ min(currentUpperBound, maximum)
	double value;
	
	// Stardate the damage was applied. Used to timeout/repair damage.
	int timeOfDamage;
	
	// Indicates if the setting is disabled as the result of damage (as opposed to just reduced by currentUpperBound)
	boolean broken;
	
	public Setting(double maximum) {
		this.maximum = maximum;
		this.currentUpperBound = maximum;
		this.value = maximum;
		this.broken = false;
	}

	public Setting(double maximum, double defaultValue) {
		this(maximum);
		setValue(defaultValue);
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
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
	
	public void damage(double delta, int timeOfDamage) {
		setCurrentUpperBound(Math.max(0,getCurrentUpperBound()-delta));
		setValue(Math.min(getValue(), getCurrentUpperBound()));
		setTimeOfDamage(timeOfDamage);
	}
	
	public void damageAndTurnOff(int timeOfDamage) {
		setBroken(true);
		setTimeOfDamage(timeOfDamage);
	}
	
	public void reset() {
		setValue(getMaximum());
	}

	public boolean getBooleanValue() {
		return getValue()>0;
	}
	
	public void setValue(boolean v) {
		setValue(v?1:0);
	}
	
	public long getTimeOfDamage() {
		return timeOfDamage;
	}
	
	public void setTimeOfDamage(int timeOfDamage) {
		this.timeOfDamage = timeOfDamage;
	}
	
	public boolean isOperational() {
		return !isBroken() && getValue()>0;
	}
	
	public boolean repair() {
		boolean neededRepair = false;
		if (getCurrentUpperBound()<getMaximum() || isBroken()) {
			setCurrentUpperBound(getMaximum());
			broken = false;
			neededRepair = true;
		}
		reset();
		return neededRepair;
	}
	
	public boolean isBroken() {
		return broken;
	}
	
	public void setBroken(boolean broken) {
		this.broken = broken;
	}
	
	@Override
	public String toString() {
		return ""+getValue()+"/"+getCurrentUpperBound()+"/"+getMaximum();
	}
}
