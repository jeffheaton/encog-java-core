package org.encog.mathutil.randomize.factory;

import java.util.Random;

public interface RandomFactory {
	 Random factor();
	 RandomFactory factorFactory();
}
