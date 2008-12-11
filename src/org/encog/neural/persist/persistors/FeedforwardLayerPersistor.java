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

/**
 * Persist a feedforward layer.
 * 
 * @author jheaton
 */
public class FeedforwardLayerPersistor implements Persistor {

	/**
	 * Load from the specified node.
	 * 
	 * @param layerNode
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	public EncogPersistedObject load(final Element layerNode) {
		final String str = layerNode.getAttribute("neuronCount");
		final String name = layerNode.getAttribute("name");
		final String description = layerNode.getAttribute("description");
		final int neuronCount = Integer.parseInt(str);
		final Element activationElement = XMLUtil.findElement(layerNode,
				"activation");
		final String activationName = activationElement.getAttribute("name");
		final Persistor persistor = EncogPersistedCollection
				.createPersistor(activationName);
		final FeedforwardLayer layer = new FeedforwardLayer(
				(ActivationFunction) persistor.load(activationElement),
				neuronCount);
		layer.setName(name);
		layer.setDescription(description);
		final Element matrixElement = XMLUtil.findElement(layerNode,
				"weightMatrix");
		if (matrixElement != null) {
			final Element e = XMLUtil.findElement(matrixElement, "Matrix");
			final Persistor persistor2 = EncogPersistedCollection
					.createPersistor("Matrix");
			final Matrix matrix = (Matrix) persistor2.load(e);
			layer.setMatrix(matrix);
		}
		return layer;
	}

	/**
	 * Save the specified object.
	 * 
	 * @param object
	 *            The object to save.
	 * @param hd
	 *            The XML object.
	 */
	public void save(final EncogPersistedObject object,
			final TransformerHandler hd) {

		try {
			final FeedforwardLayer layer = (FeedforwardLayer) object;

			AttributesImpl atts = EncogPersistedCollection
					.createAttributes(layer);
			EncogPersistedCollection.addAttribute(atts, "neuronCount", ""
					+ layer.getNeuronCount());
			hd.startElement("", "", layer.getClass().getSimpleName(), atts);

			atts = new AttributesImpl();
			EncogPersistedCollection.addAttribute(atts, "native", layer
					.getActivationFunction().getClass().getName());
			EncogPersistedCollection.addAttribute(atts, "name", layer
					.getActivationFunction().getClass().getSimpleName());
			hd.startElement("", "", "activation", atts);
			hd.endElement("", "", "activation");

			if (layer.hasMatrix()) {

				final Persistor persistor = EncogPersistedCollection
						.createPersistor(layer.getMatrix().getClass()
								.getSimpleName());
				atts.clear();
				hd.startElement("", "", "weightMatrix", atts);
				persistor.save(layer.getMatrix(), hd);
				hd.endElement("", "", "weightMatrix");

			}

			hd.endElement("", "", layer.getClass().getSimpleName());
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

}
