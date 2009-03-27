package org.encog.neural.persist.persistors;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;
import org.w3c.dom.Element;

public class BasicNeuralDataSetPersistor implements Persistor {

	public EncogPersistedObject load(XMLElement node,XMLRead in) {
		String name = node.getAttributes().get("name");
		
		return null;
	}
	
	
	private void toCommas(StringBuilder result,NeuralData data)
	{
		result.setLength(0);
		for(int i=0;i<data.size();i++)
		{
			if( i!=0 )
				result.append(',');
			result.append(data.getData(i));
		}
	}
	
	public void save(EncogPersistedObject obj, XMLWrite out) {
		PersistorUtil.beginEncogObject("TrainingData", out, obj);
		NeuralDataSet set = (NeuralDataSet)obj;
		StringBuilder builder = new StringBuilder();
		
		for(NeuralDataPair pair: set)
		{
			out.beginTag("Item");
			
			toCommas(builder,pair.getInput());
			out.addProperty("Input", builder.toString());
			
			if( pair.getIdeal()!=null )
			{
				toCommas(builder,pair.getIdeal());
				out.addProperty("Ideal", builder.toString());
			}
			out.endTag();
		}
		out.endTag();
		
	}

}
