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
package org.encog.util.randomize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A randomizer that will create random weight and threshold values that are
 * between a specified range.
 * 
 * @author jheaton
 * 
 */
public class RangeRandomizer extends BasicRandomizer {

	/**
	 * Generate a random number in the specified range.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @return A random number.
	 */
	public static double randomize(final double min, final double max) {
		final double range = max - min;
		return (range * Math.random()) + min;
	}

	/**
	 * The minimum value for the random range.
	 */
	private final double min;

	/**
	 * The maximum value for the random range.
	 */
	private final double max;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a range randomizer.
	 * @param min The minimum random value.
	 * @param max The maximum random value.
	 */
	public RangeRandomizer(final double min, final double max) {
		this.max = max;
		this.min = min;
	}

	/**
	 * Generate a random number based on the range specified in the constructor.
	 * @param d The range randomizer ignores this value.
	 * @return The random number.
	 */
	public double randomize(final double d) {
		return RangeRandomizer.randomize(this.min, this.max);
	}

}
