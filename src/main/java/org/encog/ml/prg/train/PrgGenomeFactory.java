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
