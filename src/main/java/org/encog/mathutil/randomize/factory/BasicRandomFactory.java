package org.encog.mathutil.randomize.factory;

import java.util.Random;

public class BasicRandomFactory implements RandomFactory {

	private Random seedProducer;
	
	public BasicRandomFactory() {
		this.seedProducer = new Random();
	}
	
	public BasicRandomFactory(long theSeed) {
		this.seedProducer = new Random(theSeed);
	}
	
	@Override
	public Random factor() {
		synchronized(this) {
			long seed = this.seedProducer.nextLong();
			return new Random(seed);
		}
	}

	@Override
	public RandomFactory factorFactory() {
		return new BasicRandomFactory(this.seedProducer.nextLong());
	}

}
