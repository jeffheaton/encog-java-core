package org.encog.neural.persist;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.NeuralNetworkException;
import org.w3c.dom.Element;

public interface Persistor {
	public void save(EncogPersistedObject object,TransformerHandler hd) throws NeuralNetworkException;
	public EncogPersistedObject load(Element node)throws NeuralNetworkException;
}
