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

package org.encog.neural.persist.persistors;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class BasicNeuralDataSetPersistor implements Persistor {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public EncogPersistedObject load(XMLElement node,XMLRead in) {
		String name = node.getAttributes().get("name");
		
		return null;
	}
	
	
	private void toCommas(StringBuilder result,NeuralData data)
	{
		result.setLength(0);
		for(int i=0;i<data.size();i++)
		{
			if( i!=0 )
				result.append(',');
			result.append(data.getData(i));
		}
	}
	
	public void save(EncogPersistedObject obj, XMLWrite out) {
		PersistorUtil.beginEncogObject("TrainingData", out, obj);
		NeuralDataSet set = (NeuralDataSet)obj;
		StringBuilder builder = new StringBuilder();
		
		for(NeuralDataPair pair: set)
		{
			out.beginTag("Item");
			
			toCommas(builder,pair.getInput());
			out.addProperty("Input", builder.toString());
			
			if( pair.getIdeal()!=null )
			{
				toCommas(builder,pair.getIdeal());
				out.addProperty("Ideal", builder.toString());
			}
			out.endTag();
		}
		out.endTag();
		
	}

}
