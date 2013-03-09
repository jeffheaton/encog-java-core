package org.encog.ml.factory.method;

import java.util.Map;
import java.util.Random;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.generator.PrgGrowGenerator;
import org.encog.ml.prg.score.ZeroEvalScoreFunction;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.util.ParamsHolder;

public class EPLFactory {
	/**
	 * Create a feed forward network.
	 * @param architecture The architecture string to use.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The feedforward network.
	 */
	public MLMethod create(final String architecture, final int input,
			final int output) {
		
		if( input<=0 ) {
			throw new EncogError("Must have at least one input for NEAT.");
		}
		
		if( output<=0 ) {
			throw new EncogError("Must have at least one output for NEAT.");
		}
		
		
		final Map<String, String> args = ArchitectureParse.parseParams(architecture);
		final ParamsHolder holder = new ParamsHolder(args);
		
		final int populationSize = holder.getInt(
				MLMethodFactory.PROPERTY_POPULATION_SIZE, false, 1000);
		
		EncogProgramContext context = new EncogProgramContext();
		context.defineVariable("x");

		StandardExtensions.createNumericOperators(context.getFunctions());
		PrgPopulation pop = new PrgPopulation(context,populationSize);
		(new PrgGrowGenerator(context,new ZeroEvalScoreFunction(),5)).generate(new Random(), pop);
		return pop;
	}
}
