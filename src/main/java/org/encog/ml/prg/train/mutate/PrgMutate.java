package org.encog.ml.prg.train.mutate;

import org.encog.ml.prg.EncogProgram;

public interface PrgMutate {
	void mutateSelf(EncogProgram program);
	EncogProgram mutate(EncogProgram program);
}
