package org.encog.ml.prg.train.mutate;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;

public interface PrgMutate {
	void mutateSelf(Random rnd, EncogProgram program);
	EncogProgram mutate(Random rnd, EncogProgram program);
}
