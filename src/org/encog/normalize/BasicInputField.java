package org.encog.normalize;

public class BasicInputField implements InputField {

	
	private double min = Double.POSITIVE_INFINITY;
	private double max = Double.NEGATIVE_INFINITY;
	private double currentValue;
	
	@Override
	public void applyMinMax(double d) {
		this.min = Math.min(this.min, d);
		this.max = Math.max(this.max, d);
		
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	
	
	

}
