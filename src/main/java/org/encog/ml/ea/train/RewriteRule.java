package org.encog.ml.ea.train;

import org.encog.ml.ea.genome.Genome;

public interface RewriteRule {
	boolean rewrite(Genome program);
}
