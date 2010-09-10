/*
 * Encog(tm) Core v2.5
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 *
 * Copyright 2008-2010 by Heaton Research Inc.
 *
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 *
 * http://www.heatonresearch.com/copyright.html
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
