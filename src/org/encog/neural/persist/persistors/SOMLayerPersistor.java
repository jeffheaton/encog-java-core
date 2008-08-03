package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.networks.layers.SOMLayer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.encog.util.NormalizeInput.NormalizationType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SOMLayerPersistor implements Persistor {
	
	public static String NORM_TYPE_MULTIPLICATIVE = "MULTIPLICATIVE";
	public static String NORM_TYPE_Z_AXIS = "Z_AXIS";
	
	public EncogPersistedObject load(Element layerNode)
			throws NeuralNetworkException {
		String str = layerNode.getAttribute("neuronCount");
		String normType = layerNode.getAttribute("normalization");
		int neuronCount = Integer.parseInt(str);

		SOMLayer layer;
		
		if( normType.equals(SOMLayerPersistor.NORM_TYPE_MULTIPLICATIVE))
			layer = new SOMLayer(neuronCount, NormalizationType.MULTIPLICATIVE);
		else if( normType.equals(SOMLayerPersistor.NORM_TYPE_Z_AXIS))
			layer = new SOMLayer(neuronCount, NormalizationType.Z_AXIS);
		else
			layer = null;
		
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
			SOMLayer layer = (SOMLayer) object;

			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "", "neuronCount", "CDATA", ""
					+ layer.getNeuronCount());
			
			String normType = null;
			
			if( layer.getNormalizationType()== NormalizationType.MULTIPLICATIVE )
				normType = SOMLayerPersistor.NORM_TYPE_MULTIPLICATIVE;
			else if( layer.getNormalizationType()== NormalizationType.Z_AXIS )
				normType = SOMLayerPersistor.NORM_TYPE_Z_AXIS;
			
			if( normType==null)
			{
				throw new NeuralNetworkError("Unknown normalization type");
			}
			
			atts.addAttribute("", "", "normalization", "CDATA", ""
					+ normType );
			
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
