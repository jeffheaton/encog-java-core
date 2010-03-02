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

package org.encog.persist.persistors;

import java.util.Map;

import org.encog.neural.activation.ActivationGaussian;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * The Encog persistor used to persist the ActivationGaussian class.
 * 
 * @author jheaton
 */
public class ActivationGaussianPersistor implements Persistor {

	/**
	 * THe center attribute.
	 */
	public static final String ATTRIBUTE_CENTER = "center";

	/**
	 * The peak attribute.
	 */
	public static final String ATTRIBUTE_PEAK = "peak";

	/**
	 * The width attribute.
	 */
	public static final String ATTRIBUTE_WIDTH = "width";

	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		final Map<String, String> map = in.readPropertyBlock();
		final double center = Double.parseDouble(map
				.get(ActivationGaussianPersistor.ATTRIBUTE_CENTER));
		final double peak = Double.parseDouble(map
				.get(ActivationGaussianPersistor.ATTRIBUTE_PEAK));
		final double width = Double.parseDouble(map
				.get(ActivationGaussianPersistor.ATTRIBUTE_WIDTH));
		return new ActivationGaussian(center, peak, width);
	}

	/**
	 * Save the specified Encog object to an XML writer.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer to save to.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {
		final ActivationGaussian g = (ActivationGaussian) obj;
		out.beginTag(obj.getClass().getSimpleName());
		out.addProperty(ActivationGaussianPersistor.ATTRIBUTE_CENTER, g
				.getGausian().getCenter());
		out.addProperty(ActivationGaussianPersistor.ATTRIBUTE_PEAK, g
				.getGausian().getPeak());
		out.addProperty(ActivationGaussianPersistor.ATTRIBUTE_WIDTH, g
				.getGausian().getWidth());
		out.endTag();
	}

}
