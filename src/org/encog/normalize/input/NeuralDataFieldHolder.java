package org.encog.normalize.input;

import java.util.Iterator;

import org.encog.neural.data.NeuralDataPair;

public class NeuralDataFieldHolder {
	private NeuralDataPair pair;
	private final Iterator<NeuralDataPair> iterator;
	private final InputFieldNeuralDataSet field;

	public NeuralDataFieldHolder(final Iterator<NeuralDataPair> iterator,
			final InputFieldNeuralDataSet field) {
		super();
		this.iterator = iterator;
		this.field = field;
	}

	public InputFieldNeuralDataSet getField() {
		return this.field;
	}

	public Iterator<NeuralDataPair> getIterator() {
		return this.iterator;
	}

	public NeuralDataPair getPair() {
		return this.pair;
	}

	public void obtainPair() {
		this.pair = this.iterator.next();
	}

	public void setPair(final NeuralDataPair pair) {
		this.pair = pair;
	}
}
