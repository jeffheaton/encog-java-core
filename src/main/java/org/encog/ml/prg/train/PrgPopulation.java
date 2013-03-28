package org.encog.ml.prg.train;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.parse.expression.common.RenderCommonExpression;

public class PrgPopulation extends BasicPopulation implements MLRegression {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	private final EncogProgramContext context;

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
		EncogProgram best = (EncogProgram)getBestGenome();
		return best.compute(input);
	}

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

	public EncogProgramContext getContext() {
		return this.context;
	}


	@Override
	public int getInputCount() {
		return this.getContext().getDefinedVariables().size();
	}

	@Override
	public int getOutputCount() {
		return 1;
	}
}
