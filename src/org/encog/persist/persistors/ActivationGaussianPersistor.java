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
package org.encog.persist.persistors;

import java.util.Map;

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.activation.ActivationGaussian;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

public class ActivationGaussianPersistor implements Persistor {

	public final static String ATTRIBUTE_CENTER = "center";
	public final static String ATTRIBUTE_PEAK = "peak";
	public final static String ATTRIBUTE_WIDTH = "width";
	
	@Override
	public EncogPersistedObject load(ReadXML in) {
		Map<String, String> map = in.readPropertyBlock();
		double center = Double.parseDouble(map.get(ATTRIBUTE_CENTER));
		double peak = Double.parseDouble(map.get(ATTRIBUTE_PEAK));
		double width = Double.parseDouble(map.get(ATTRIBUTE_WIDTH));
		return new ActivationGaussian(center, peak, width);
	}

	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {
		out.beginTag(obj.getClass().getSimpleName());
		ActivationGaussian g = (ActivationGaussian)obj;
		out.addProperty(ATTRIBUTE_CENTER, g.getGausian().getCenter());
		out.addProperty(ATTRIBUTE_PEAK, g.getGausian().getPeak());
		out.addProperty(ATTRIBUTE_WIDTH, g.getGausian().getWidth());
		out.endTag();		
	}

}
