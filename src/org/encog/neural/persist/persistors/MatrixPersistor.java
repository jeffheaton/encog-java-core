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
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Persist a matrix.
 * 
 * @author jheaton
 * 
 */
public class MatrixPersistor implements Persistor {

	/**
	 * Load from the specified node.
	 * 
	 * @param matrixElement
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	public EncogPersistedObject load(final Element matrixElement) {
		final String name = matrixElement.getAttribute("name");
		final String description = matrixElement.getAttribute("description");
		final int rows = Integer.parseInt(matrixElement.getAttribute("rows"));
		final int cols = Integer.parseInt(matrixElement.getAttribute("cols"));
		final Matrix result = new Matrix(rows, cols);

		result.setName(name);
		result.setDescription(description);

		int row = 0;

		for (Node child = matrixElement.getFirstChild(); child != null; 
			child = child.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}
			final Element node = (Element) child;
			if (node.getNodeName().equals("row")) {
				for (int col = 0; col < cols; col++) {
					final double value = Double.parseDouble(node
							.getAttribute("col" + col));
					result.set(row, col, value);
				}
				row++;
			}
		}

		return result;
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
			final Matrix matrix = (Matrix) object;
			final AttributesImpl atts = EncogPersistedCollection
					.createAttributes(object);
			EncogPersistedCollection.addAttribute(atts, "rows", ""
					+ matrix.getRows());
			EncogPersistedCollection.addAttribute(atts, "cols", ""
					+ matrix.getCols());

			hd.startElement("", "", "Matrix", atts);
			for (int row = 0; row < matrix.getRows(); row++) {
				atts.clear();
				for (int col = 0; col < matrix.getCols(); col++) {
					atts.addAttribute("", "", "col" + col, "CDATA", ""
							+ matrix.get(row, col));
				}

				hd.startElement("", "", "row", atts);
				hd.endElement("", "", "row");
			}
			hd.endElement("", "", "Matrix");
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

}
