package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.NeuralNetworkException;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.w3c.dom.Element;

public class ActivationLinearPersistor implements Persistor  {

	public EncogPersistedObject load(Element node)
			throws NeuralNetworkException {
		return new ActivationLinear();
	}

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkException {
		// TODO Auto-generated method stub
		
	}

}
