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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.encog.parse.Parse;
import org.encog.parse.recognize.Recognize;
import org.encog.parse.recognize.RecognizeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage the unit types supported by Encog.
 * 
 * @author jheaton
 * 
 */
public class UnitManager {

	/**
	 * The base weight.
	 */
	public static final String BASE_WEIGHT = "base-weight";

	/**
	 * Supported unit conversions.
	 */
	private final Collection<UnitConversion> conversions = 
		new ArrayList<UnitConversion>();

	/**
	 * Supported aliases for each supported unit type.
	 */
	private final Map<String, String> aliases = new HashMap<String, String>();

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Convert the specified unit.
	 * 
	 * @param from
	 *            The from unit.
	 * @param to
	 *            The to unit.
	 * @param input
	 *            The value to convert.
	 * @return The converted unit.
	 */
	public double convert(final String from, final String to, 
			final double input) {
		final String resolvedFrom = resolveAlias(from);
		final String resolvedTo = resolveAlias(to);

		for (final UnitConversion convert : this.conversions) {
			if (convert.getFrom().equals(resolvedFrom)
					&& convert.getTo().equals(resolvedTo)) {
				return convert.convert(input);
			}
		}
		return 0;
	}

	/**
	 * Create recongizers for the specified parse object.
	 * 
	 * @param parse
	 *            The parse object to create recognizers for.
	 */
	public void createRecognizers(final Parse parse) {
		final Map<Object, Object> map = new HashMap<Object, Object>();

		// put everything in a map to eliminate duplicates
		// create the recognizers
		final Recognize weightRecognize = parse.getTemplate().createRecognizer(
				"weightUnit");
		final RecognizeElement weightElement = weightRecognize
				.createElement(RecognizeElement.ALLOW_ONE);

		// get all of the units
		for (final UnitConversion unit : this.conversions) {
			map.put(unit.getFrom(), null);
			map.put(unit.getTo(), null);
		}

		// get all of the aliases
		for (final Map.Entry<String, String> entry : this.aliases.entrySet()) {
			map.put(entry.getKey(), null);
			map.put(entry.getValue(), null);
		}

		// now add all of the units to the correct recognizers
		for (final Map.Entry<String, String> entry : this.aliases.entrySet()) {
			weightElement.addAcceptedSignal("word", entry.getKey().toString());
		}
	}

	/**
	 * Resolve the specified alias.
	 * 
	 * @param in
	 *            The alias to look up.
	 * @return The alias resolved.
	 */
	public String resolveAlias(final String in) {
		final String base = this.aliases.get(in.toLowerCase());
		if (base == null) {
			return in;
		} else {
			return base;
		}
	}
}
