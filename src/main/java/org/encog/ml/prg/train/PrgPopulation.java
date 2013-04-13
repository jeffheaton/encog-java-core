package org.encog.ml.prg.train;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.parse.expression.common.RenderCommonExpression;

/**
 * A population that contains EncogProgram's. The primary difference between
 * this class and BasicPopulation is that a "compute" method is provided that
 * automatically uses the "best" genome to provide a MLRegression compute
 * method. This population type also holds the common context that all of the
 * EncogProgram genomes make use of.
 */
public class PrgPopulation extends BasicPopulation implements MLRegression {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The context.
	 */
	private final EncogProgramContext context;

	/**
	 * Construct the population.
	 * 
	 * @param theContext
	 *            The context.
	 * @param thePopulationSize
	 *            The population size.
	 */
	public PrgPopulation(final EncogProgramContext theContext,
			final int thePopulationSize) {
		super(thePopulationSize, new PrgGenomeFactory(theContext));
		this.context = theContext;
	}

	/**
	 * Compute the output from the best Genome. Note: it is not safe to call
	 * this method while training is progressing.
	 * 
	 * @param input
	 *            The input to the
	 */
	@Override
	public MLData compute(final MLData input) {
		final EncogProgram best = (EncogProgram) getBestGenome();
		return best.compute(input);
	}

	/**
	 * Dump the specified number of genomes.
	 * 
	 * @param i
	 *            The specified number of genomes.
	 */
	public void dumpMembers(final int i) {

		final RenderCommonExpression render = new RenderCommonExpression();

		int index = 0;
		for (final Species species : getSpecies()) {
			System.out.println("** Species: " + species.toString());
			for (final Genome obj : species.getMembers()) {
				final EncogProgram prg = (EncogProgram) obj;
				System.out.println(index + ": Score " + prg.getScore() + " : "
						+ render.render(prg));
				index++;
				if (index > i) {
					break;
				}
			}
		}
	}

	/**
	 * @return The context for the programs.
	 */
	public EncogProgramContext getContext() {
		return this.context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return getContext().getDefinedVariables().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		return 1;
	}
}
