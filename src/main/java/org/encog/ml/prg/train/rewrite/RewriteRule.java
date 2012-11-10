package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;

public interface RewriteRule {
	void rewrite(EncogProgram program);
}
