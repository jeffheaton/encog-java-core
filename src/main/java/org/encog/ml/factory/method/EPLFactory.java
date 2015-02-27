/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.factory.method;

import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.generator.RampedHalfAndHalf;
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
			StandardExtensions.createNumericOperators(context);
		}
		
		PrgPopulation pop = new PrgPopulation(context,populationSize);
		
		if( context.getFunctions().size()>0 ) {
			(new RampedHalfAndHalf(context,2,6)).generate(new Random(), pop);
		}
		return pop;
	}
}
