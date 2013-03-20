package org.encog.ml.factory.method;

import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.fitness.ZeroEvalScoreFunction;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.generator.PrgGrowGenerator;
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
			throw new EncogError("Must have at least one input for EPL.");
		}
		
		if( output<=0 ) {
			throw new EncogError("Must have at least one output for EPL.");
		}
		
		
		final Map<String, String> args = ArchitectureParse.parseParams(architecture);
		final ParamsHolder holder = new ParamsHolder(args);
		
		final int populationSize = holder.getInt(
				MLMethodFactory.PROPERTY_POPULATION_SIZE, false, 1000);
		String variables = holder.getString("vars", false, "x");
		String funct = holder.getString("funct", false, null);
		
		EncogProgramContext context = new EncogProgramContext();
		StringTokenizer tok = new StringTokenizer(variables,",");
		while(tok.hasMoreElements()) {
			context.defineVariable(tok.nextToken());
		}

		if( "numeric".equalsIgnoreCase(funct) ) {
			StandardExtensions.createNumericOperators(context.getFunctions());
		}
		
		PrgPopulation pop = new PrgPopulation(context,populationSize);
		
		if( context.getFunctions().size()>0 ) {
			(new PrgGrowGenerator(context,5)).generate(new Random(), pop,new ZeroEvalScoreFunction());
		}
		return pop;
	}
}
