/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.parse.units;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to provide unit conversion for the parser.
 * 
 * @author jheaton
 * 
 */
public class UnitConversion {
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The from unit.
	 */
	private final String from;
	
	/**
	 * The to unit.
	 */
	private final String to;
	
	/**
	 * The number to add before the ratio.
	 */
	private final double addPreRatio;
	
	/**
	 * The number to add after the ratio.
	 */
	private final double addPostRatio;
	
	/**
	 * The conversion ratio.
	 */
	private final double ratio;

	/**
	 * Used to specify how a unit conversion works.
	 * @param from The from unit.
	 * @param to The to unit.
	 * @param addPreRatio The number to be added before the ratio.
	 * @param addPostRatio The number to be added after the ratio.
	 * @param ratio The ratio.
	 */
	public UnitConversion(final String from, final String to,
			final double addPreRatio, final double addPostRatio,
			final double ratio) {
		this.from = from;
		this.to = to;
		this.addPreRatio = addPreRatio;
		this.addPostRatio = addPostRatio;
		this.ratio = ratio;
	}

	/**
	 * Perform the conversion.
	 * @param input The number to convert.
	 * @return The converted value.
	 */
	public double convert(final double input) {
		return (((input + this.addPreRatio) * this.ratio) + this.addPostRatio);
	}

	/**
	 * @return The value to add before the ratio is applied.
	 */
	public double getAddPostRatio() {
		return this.addPostRatio;
	}

	/**
	 * @return The value to add after the ratio is applied.
	 */
	public double getAddPreRatio() {
		return this.addPreRatio;
	}

	/**
	 * @return The conversion ratio.
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * @return The conversion ratio.
	 */
	public double getRatio() {
		return this.ratio;
	}

	/**
	 * @return The to unit.
	 */
	public String getTo() {
		return this.to;
	}
}
