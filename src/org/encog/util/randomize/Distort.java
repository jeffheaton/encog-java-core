package org.encog.util.randomize;

public class Distort extends BasicRandomizer {

	double factor;
	
	public Distort(double factor)
	{
		this.factor = factor;
	}
	
	public double randomize(double d) {
		return d+ (this.factor-(Math.random()*this.factor*2));
	}

}
