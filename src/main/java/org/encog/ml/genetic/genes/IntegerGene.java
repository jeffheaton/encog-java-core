/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.genetic.genes;


/**
 * A gene that contains an integer value.
 */
public class IntegerGene extends BasicGene {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The value of this gene.
	 */
	private int value;

	/**
	 * Copy another gene to this one.
	 *
	 * @param gene
	 *            The other gene to copy.
	 */
	public final void copy(final Gene gene) {
		this.value = ((IntegerGene) gene).getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof IntegerGene) {
			return (((IntegerGene) obj).getValue() == this.value);
		} else {
			return false;
		}
	}

	/**
	 * @return The value of this gene.
	 */
	public final int getValue() {
		return this.value;
	}

	/**
	 * @return a hash code.
	 */
	@Override
	public final int hashCode() {
		return this.value;
	}

	/**
	 * Set the value of this gene.
	 *
	 * @param theValue
	 *            The value of this gene.
	 */
	public final void setValue(final int theValue) {
		this.value = theValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "" + this.value;
	}
}
