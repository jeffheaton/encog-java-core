package org.encog.util.randomize;

public class RangeRandomizer extends BasicRandomizer {

	final double min;
	final double max;
	
	public RangeRandomizer(final double min, final double max)
	{
		this.max = max;
		this.min = min;
	}
	
	public double randomize(double d) {
		double range = max-min;
		return (range*Math.random())+this.min;
	}

}
