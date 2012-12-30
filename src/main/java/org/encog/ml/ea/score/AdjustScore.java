package org.encog.ml.ea.score;

import org.encog.ml.ea.genome.Genome;

public interface AdjustScore {
	double calculateAdjustment(Genome genome);
}
