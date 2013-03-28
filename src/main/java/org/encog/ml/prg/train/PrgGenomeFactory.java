package org.encog.ml.prg.train;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;

public class PrgGenomeFactory implements GenomeFactory, Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	private final EncogProgramContext context;

	public PrgGenomeFactory(final EncogProgramContext theContext) {
		this.context = theContext;
	}

	@Override
	public Genome factor() {
		final EncogProgram result = new EncogProgram(this.context,
				new EncogProgramVariables());
		return result;
	}

	@Override
	public Genome factor(final Genome other) {
		final EncogProgram result = new EncogProgram(this.context,
				new EncogProgramVariables());
		result.copy(other);
		return result;
	}
}
