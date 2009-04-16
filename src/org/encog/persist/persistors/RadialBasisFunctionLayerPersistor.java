package org.encog.persist.persistors;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.ReadCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Encog persistor used to persist the RadialBasisFunctionLayer class.
 * 
 * @author jheaton
 */
public class RadialBasisFunctionLayerPersistor implements Persistor {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public EncogPersistedObject load(ReadXML in) {
		
		int neuronCount = 0;

		String end = in.getTag().getName();
		
		while( in.readToTag() ) 
		{
			if( in.is(BasicLayerPersistor.PROPERTY_NEURONS,true))
			{
				neuronCount = in.readIntToTag();
			}
			if( in.is(end, false))
				break;
		}
		
		if( neuronCount>0)
		{			
			RadialBasisFunctionLayer layer; 
			
			layer = new RadialBasisFunctionLayer(neuronCount);
						
			return layer;
		}
		return null;
	}

	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_RADIAL_BASIS_LAYER, out, obj, false);
		BasicLayer layer = (BasicLayer)obj;
		out.addProperty(BasicLayerPersistor.PROPERTY_NEURONS, layer.getNeuronCount());
		if( layer.hasThreshold())
		{
			StringBuilder result = new StringBuilder();
			ReadCSV.toCommas(result, layer.getThreshold());
			out.addProperty(BasicLayerPersistor.PROPERTY_THRESHOLD,result.toString() );
		}
		
		out.beginTag(BasicLayerPersistor.TAG_ACTIVATION);
		Persistor persistor = layer.getActivationFunction().createPersistor();
		persistor.save(layer.getActivationFunction(), out);
		out.endTag();
				
		out.endTag();		
	}

}
