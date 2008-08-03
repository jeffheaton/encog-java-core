package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkException;
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
			throws NeuralNetworkException {
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
			throws NeuralNetworkException {

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
