package org.encog.mathutil.randomize.factory;

import java.util.Random;

public class BasicRandomFactory implements RandomFactory {

	private long seed = 0;
	
	public BasicRandomFactory() {
		this(0);
	}
	
	public BasicRandomFactory(long theSeed) {
		this.seed = theSeed;
	}
	
	@Override
	public Random factor() {
		if( seed==0 ) {
			return new Random();
		} else {
			return new Random(seed);
		}
	}

	@Override
	public RandomFactory factorFactory() {
		return new BasicRandomFactory(this.seed);
	}

}
