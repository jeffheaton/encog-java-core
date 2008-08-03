package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Layer;
import org.encog.neural.networks.layers.FeedforwardLayer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class BasicNetworkPersistor implements Persistor {

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkException {
		try {
			AttributesImpl atts = new AttributesImpl();
			BasicNetwork network = (BasicNetwork) object;
			hd.startElement("", "", network.getClass().getSimpleName(), atts);
			hd.startElement("", "", "layers", atts);
			for (Layer layer : network.getLayers()) {
				if( layer instanceof EncogPersistedObject ) {
					EncogPersistedObject epo = (EncogPersistedObject)layer;
					Persistor persistor = EncogPersistedCollection.createPersistor(layer.getClass().getSimpleName());
					persistor.save(epo, hd);
				}
			}
			hd.endElement("", "", "layers");
			hd.endElement("", "", network.getClass().getSimpleName());
		} catch (SAXException e) {
			throw new NeuralNetworkException(e);
		}

	}


	public EncogPersistedObject load(Element networkNode)
			throws NeuralNetworkException {
		BasicNetwork network = new BasicNetwork();

		Element layers = XMLUtil.findElement(networkNode, "layers");
		for (Node child = layers.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;
			Persistor persistor = EncogPersistedCollection.createPersistor(node.getNodeName());
			if (persistor!=null) {
				network.addLayer((Layer)persistor.load(node));
			}
		}

		return network;
	}


}
