/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.solve.genetic.genes;

/**
 * A gene that contains a floating point value.
 */
public class DoubleGene extends BasicGene {

	/**
	 * The value of this gene.
	 */
	private double value;

	/**
	 * Copy another gene to this one.
	 * @param gene The other gene to copy.
	 */
	public void copy(final Gene gene) {
		value = ((DoubleGene) gene).getValue();

	}

	/**
	 * @return The gene value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Set the value of the gene.
	 * @param value The gene's value.
	 */
	public void setValue(final double value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "" + value;
	}
}
