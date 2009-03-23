package org.encog.neural.persist.persistors;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.xml.XMLWrite;
import org.w3c.dom.Element;

public class BasicNeuralDataSetPersistor implements Persistor {

	@Override
	public EncogPersistedObject load(Element node) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void saveNeuralData(String name, NeuralData data, XMLWrite out)
	{
		StringBuilder line = new StringBuilder();
		
		out.beginTag(name);
		for(int i=0;i<data.size();i++)
		{
			line.append(data.getData(i));
		}
		out.endTag();
	}

	@Override
	public void save(EncogPersistedObject obj, XMLWrite out) {
		PersistorUtil.beginEncogObject("TrainingData", out, obj);
		NeuralDataSet set = (NeuralDataSet)obj;
		for(NeuralDataPair pair: set)
		{
			out.beginTag("Item");
			//saveNeuralData();
			out.endTag();
		}
		out.endTag();
		
	}

}
