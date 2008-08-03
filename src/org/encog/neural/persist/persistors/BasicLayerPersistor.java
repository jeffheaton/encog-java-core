package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.HopfieldLayer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class BasicLayerPersistor implements Persistor {

	public EncogPersistedObject load(Element layerNode)
			throws NeuralNetworkException {
		String str = layerNode.getAttribute("neuronCount");
		int neuronCount = Integer.parseInt(str);

		BasicLayer layer = new BasicLayer(neuronCount);
		Element matrixElement = XMLUtil.findElement(layerNode, "weightMatrix");
		if (matrixElement != null) {
			Element e = XMLUtil.findElement(matrixElement, "Matrix");
			Persistor persistor = EncogPersistedCollection
					.createPersistor("Matrix");
			Matrix matrix = (Matrix) persistor.load(e);
			layer.setMatrix(matrix);
		}
		return layer;
	}

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkException {

		try {
			BasicLayer layer = (BasicLayer) object;

			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "", "neuronCount", "CDATA", ""
					+ layer.getNeuronCount());
			hd.startElement("", "", layer.getClass().getSimpleName(), atts);

			atts.clear();

			if (layer.hasMatrix()) {
				if (layer.getMatrix() instanceof EncogPersistedObject) {
					Persistor persistor = EncogPersistedCollection
							.createPersistor(layer.getMatrix().getClass()
									.getSimpleName());
					atts.clear();
					hd.startElement("", "", "weightMatrix", atts);
					persistor.save(layer.getMatrix(), hd);
					hd.endElement("", "", "weightMatrix");
				}
			}

			hd.endElement("", "", layer.getClass().getSimpleName());
		} catch (SAXException e) {
			throw new NeuralNetworkException(e);
		}
	}

}
