package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;

public interface RewriteRule {
	boolean rewrite(EncogProgram program);
}
