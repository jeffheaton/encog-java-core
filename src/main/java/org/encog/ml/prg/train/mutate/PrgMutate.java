package org.encog.ml.prg.train.mutate;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;

public interface PrgMutate {
	void mutate(Random rnd, EncogProgram program, EncogProgram[] offspring,int index, int mutationCount);
}
