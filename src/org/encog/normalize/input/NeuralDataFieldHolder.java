package org.encog.normalize.input;

import java.util.Iterator;

import org.encog.neural.data.NeuralDataPair;

public class NeuralDataFieldHolder {
	private NeuralDataPair pair;
	private final Iterator<NeuralDataPair> iterator;
	private final InputFieldNeuralDataSet field;
	
	public NeuralDataFieldHolder(Iterator<NeuralDataPair> iterator,
			InputFieldNeuralDataSet field) {
		super();
		this.iterator = iterator;
		this.field = field;
	}

	public Iterator<NeuralDataPair> getIterator() {
		return iterator;
	}

	public InputFieldNeuralDataSet getField() {
		return field;
	}

	public NeuralDataPair getPair() {
		return pair;
	}

	public void setPair(NeuralDataPair pair) {
		this.pair = pair;
	}
	
	public void obtainPair()
	{
		this.pair = this.iterator.next();
	}
}
