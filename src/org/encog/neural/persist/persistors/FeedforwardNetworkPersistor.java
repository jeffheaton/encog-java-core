package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.feedforward.FeedforwardLayer;
import org.encog.neural.networks.feedforward.FeedforwardNetwork;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class FeedforwardNetworkPersistor implements Persistor {

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkException {
		try {
			AttributesImpl atts = new AttributesImpl();
			FeedforwardNetwork network = (FeedforwardNetwork) object;
			hd.startElement("", "", network.getName(), atts);
			hd.startElement("", "", "layers", atts);
			for (FeedforwardLayer layer : network.getLayers()) {
				saveLayer(hd, layer);
			}
			hd.endElement("", "", "layers");
			hd.endElement("", "", network.getName());
		} catch (SAXException e) {
			throw new NeuralNetworkException(e);
		}

	}

	private void saveLayer(TransformerHandler hd, FeedforwardLayer layer)
			throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "neuronCount", "CDATA", ""
				+ layer.getNeuronCount());
		hd.startElement("", "", "Layer", atts);

		atts.clear();
		atts.addAttribute("", "", "native", "CDATA", layer
				.getActivationFunction().getClass().getName());
		atts.addAttribute("", "", "name", "CDATA", layer
				.getActivationFunction().getClass().getSimpleName());
		hd.startElement("", "", "activation", atts);
		hd.endElement("", "", "activation");

		if (layer.hasMatrix()) {
			atts.clear();
			hd.startElement("", "", "weightMatrix", atts);
			saveMatrix(hd, layer.getMatrix());
			hd.endElement("", "", "weightMatrix");
		}

		hd.endElement("", "", "Layer");
	}

	private void saveMatrix(TransformerHandler hd, Matrix matrix)
			throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "rows", "CDATA", "" + matrix.getRows());
		atts.addAttribute("", "", "cols", "CDATA", "" + matrix.getCols());
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
	}

	@Override
	public EncogPersistedObject load(Element networkNode)
			throws NeuralNetworkException {
		FeedforwardNetwork network = new FeedforwardNetwork();

		Element layers = XMLUtil.findElement(networkNode, "layers");
		for (Node child = layers.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;
			if (node.getNodeName().equals("Layer")) {
				network.addLayer(loadLayer(node));
			}
		}

		return network;
	}

	private FeedforwardLayer loadLayer(Element layerNode)
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
			Matrix matrix = loadMatrix(e);
			layer.setMatrix(matrix);
		}
		return layer;
	}

	private Matrix loadMatrix(Element matrixElement) {
		int rows = Integer.parseInt(matrixElement.getAttribute("rows"));
		int cols = Integer.parseInt(matrixElement.getAttribute("cols"));
		Matrix result = new Matrix(rows, cols);

		int row = 0;
		
		for (Node child = matrixElement.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;
			if (node.getNodeName().equals("row")) {
				for(int col=0;col<cols;col++)
				{
					double value = Double.parseDouble(node.getAttribute("col"+col));
					result.set(row, col, value);
				}
				row++;
			}
		}

		return result;
	}

}
