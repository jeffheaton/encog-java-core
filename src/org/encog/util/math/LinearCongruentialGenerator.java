package org.encog.util.math;

public class LinearCongruentialGenerator {
	private final long modulus;
	private final long multiplier;
	private final long increment;
	private long seed;
	
	public static final long MAX_RAND = 4294967295L;
	
	public LinearCongruentialGenerator(long modulus, long multiplier,
			long increment, long seed) {
		super();
		this.modulus = modulus;
		this.multiplier = multiplier;
		this.increment = increment;
		this.seed = seed;
	}
	
	public LinearCongruentialGenerator(long seed)
	{
		this((long)Math.pow(2L, 32L),1103515245L,12345L,seed);
	}

	public long getModulus() {
		return modulus;
	}

	public long getMultiplier() {
		return multiplier;
	}

	public long getIncrement() {
		return increment;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public long nextLong()
	{
		this.seed = (this.multiplier*this.seed+this.increment)%this.modulus;
		return this.seed;
	}
	
	public double nextDouble()
	{
		return (double)this.nextLong()/LinearCongruentialGenerator.MAX_RAND;
	}

	public double range(double min, double max) {
		final double range = max - min;
		return (range * nextDouble()) + min;
	}
	
}
