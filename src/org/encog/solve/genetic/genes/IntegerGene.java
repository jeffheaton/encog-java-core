/*
 * Encog(tm) Core v2.4
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
 * A gene that contains an integer value.
 */
public class IntegerGene extends BasicGene {
	
	/**
	 * The value of this gene.
	 */
	private int value;

	/**
	 * Copy another gene to this one.
	 * @param gene The other gene to copy.
	 */
	public void copy(final Gene gene) {
		value = ((IntegerGene) gene).getValue();
	}
	
	public int hashCode()
	{
		return value;
	}

	/**
	 * Determine if this gene has the same values as another.
	 * @param obj The other gene.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof IntegerGene) {
			return (((IntegerGene) obj).getValue() == value);
		} else {
			return false;
		}
	}

	/**
	 * @return The value of this gene.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the value of this gene.
	 * @param value The value of this gene.
	 */
	public void setValue(final int value) {
		this.value = value;
	}

	/**
	 * @return The gene as a string.
	 */
	@Override
	public String toString() {
		return "" + value;
	}

}
