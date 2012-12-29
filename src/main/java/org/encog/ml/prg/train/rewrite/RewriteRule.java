package org.encog.ml.prg.train.rewrite;

import org.encog.ml.ea.genome.Genome;

public interface RewriteRule {
	boolean rewrite(Genome program);
}
