/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.layers.FeedforwardLayer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class FeedforwardLayerPersistor implements Persistor {

	public EncogPersistedObject load(Element layerNode)
			throws NeuralNetworkError {
		String str = layerNode.getAttribute("neuronCount");
		int neuronCount = Integer.parseInt(str);
		Element activationElement = XMLUtil
				.findElement(layerNode, "activation");
		String activationName = activationElement.getAttribute("name");
		Persistor persistor = EncogPersistedCollection
				.createPersistor(activationName);
		FeedforwardLayer layer = new FeedforwardLayer(
				(ActivationFunction) persistor.load(activationElement),
				neuronCount);
		Element matrixElement = XMLUtil.findElement(layerNode, "weightMatrix");
		if (matrixElement != null) {
			Element e = XMLUtil.findElement(matrixElement, "Matrix");
			Persistor persistor2 = EncogPersistedCollection.createPersistor("Matrix");
			Matrix matrix = (Matrix)persistor2.load(e);
			layer.setMatrix(matrix);
		}
		return layer;
	}
	

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkError {

		try {
			FeedforwardLayer layer = (FeedforwardLayer) object;

			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "", "neuronCount", "CDATA", ""
					+ layer.getNeuronCount());
			hd.startElement("", "", layer.getClass().getSimpleName(), atts);

			atts.clear();
			atts.addAttribute("", "", "native", "CDATA", layer
					.getActivationFunction().getClass().getName());
			atts.addAttribute("", "", "name", "CDATA", layer
					.getActivationFunction().getClass().getSimpleName());
			hd.startElement("", "", "activation", atts);
			hd.endElement("", "", "activation");

			if (layer.hasMatrix()) {
				
					Persistor persistor = EncogPersistedCollection
							.createPersistor(layer.getMatrix().getClass()
									.getSimpleName());
					atts.clear();
					hd.startElement("", "", "weightMatrix", atts);
					persistor.save(layer.getMatrix(), hd);
					hd.endElement("", "", "weightMatrix");
				
			}

			hd.endElement("", "", layer.getClass().getSimpleName());
		} catch (SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

}
