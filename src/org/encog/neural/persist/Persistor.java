package org.encog.neural.persist;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.NeuralNetworkException;

public interface Persistor {
	public void save(EncogPersistedObject object,TransformerHandler hd) throws NeuralNetworkException;
}
