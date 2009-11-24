package org.encog.neural.networks.training;

import org.encog.neural.networks.BasicNetwork;

public interface CalculateScore {
	double calculateScore(BasicNetwork network);
	boolean shouldMinimize();
}
