package org.encog.ml.ea.rules;

import org.encog.ml.ea.genome.Genome;

public interface ConstraintRule {
	boolean isValid(Genome genome);
}
