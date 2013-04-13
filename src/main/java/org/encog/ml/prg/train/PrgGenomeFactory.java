package org.encog.ml.prg.train;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;

/**
 * A GenomeFactory that creates EncogProgram genomes.
 */
public class PrgGenomeFactory implements GenomeFactory, Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The context.
	 */
	private final EncogProgramContext context;

	/**
	 * Construct a factory.
	 * 
	 * @param theContext
	 *            The context to use.
	 */
	public PrgGenomeFactory(final EncogProgramContext theContext) {
		this.context = theContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor() {
		final EncogProgram result = new EncogProgram(this.context,
				new EncogProgramVariables());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor(final Genome other) {
		final EncogProgram result = new EncogProgram(this.context,
				new EncogProgramVariables());
		result.copy(other);
		return result;
	}
}
