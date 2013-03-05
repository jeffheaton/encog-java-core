package org.encog.ml.prg.train;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;

public class PrgGenomeFactory implements GenomeFactory, Serializable {

	private final EncogProgramContext context;

	public PrgGenomeFactory(final EncogProgramContext theContext) {
		this.context = theContext;
	}

	@Override
	public Genome factor() {
		final EPLHolder newHolder = this.context.getHolderFactory().factor(1,
				this.context.getParams().getMaxIndividualSize());
		final EncogProgram result = new EncogProgram(this.context,
				new EncogProgramVariables(), newHolder, 0);
		return result;
	}

	@Override
	public Genome factor(final Genome other) {
		final EPLHolder newHolder = this.context.getHolderFactory().factor(1,
				this.context.getParams().getMaxIndividualSize());
		final EncogProgram result = new EncogProgram(this.context,
				new EncogProgramVariables(), newHolder, 0);
		result.copy(other);
		return result;
	}
}
