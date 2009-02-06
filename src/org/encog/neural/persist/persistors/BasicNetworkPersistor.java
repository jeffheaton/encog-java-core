/*
 * Encog Artificial Intelligence Framework v1.x
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

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Layer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Persist the basic network.
 * 
 * @author jheaton
 */
public class BasicNetworkPersistor implements Persistor {

	/**
	 * Save the specified object.
	 * 
	 * @param networkNode
	 *            The node to load from.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final Element networkNode) {
		final BasicNetwork network = new BasicNetwork();

		final String name = networkNode.getAttribute("name");
		final String description = networkNode.getAttribute("description");
		network.setName(name);
		network.setDescription(description);

		final Element layers = XMLUtil.findElement(networkNode, "layers");
		for (Node child = layers.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}
			final Element node = (Element) child;
			final Persistor persistor = EncogPersistedCollection
					.createPersistor(node.getNodeName());
			if (persistor != null) {
				network.addLayer((Layer) persistor.load(node));
			}
		}

		return network;
	}

	/**
	 * Save the specified object.
	 * 
	 * @param object
	 *            The node to load from.
	 * @param hd
	 *            The XML object.
	 */
	public void save(final EncogPersistedObject object,
			final TransformerHandler hd) {
		try {
			final AttributesImpl atts = EncogPersistedCollection
					.createAttributes(object);

			final BasicNetwork network = (BasicNetwork) object;
			hd.startElement("", "", network.getClass().getSimpleName(), atts);
			hd.startElement("", "", "layers", atts);
			for (final Layer layer : network.getLayers()) {
				if (layer instanceof EncogPersistedObject) {
					final EncogPersistedObject epo = 
						(EncogPersistedObject) layer;
					final Persistor persistor = EncogPersistedCollection
							.createPersistor(layer.getClass().getSimpleName());
					persistor.save(epo, hd);
				}
			}
			hd.endElement("", "", "layers");
			hd.endElement("", "", network.getClass().getSimpleName());
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}

	}

}
