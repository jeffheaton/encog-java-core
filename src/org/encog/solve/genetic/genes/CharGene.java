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
 * A gene that holds a single character.
 */
public class CharGene extends BasicGene {

	/**
	 * The character value of the gene.
	 */
	private char value;

	/**
	 * Copy another gene to this gene.
	 * @param gene The source gene.
	 */
	@Override
	public void copy(final Gene gene) {
		this.value = ((CharGene) gene).getValue();
	}

	/**
	 * @return The value of this gene.
	 */
	public char getValue() {
		return this.value;
	}

	/**
	 * Set the value of this gene.
	 * @param value The new value of this gene.
	 */
	public void setValue(final char value) {
		this.value = value;
	}

	/**
	 * @return This object as a string.
	 */
	@Override
	public String toString() {
		return "" + this.value;
	}
}
