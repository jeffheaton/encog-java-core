package org.encog.normalize.target;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class NormalizationTargetNeuralDataSet implements NormalizationTarget {

	private final int inputCount;
	private final int idealCount;
	private final NeuralDataSet dataset;

	public NormalizationTargetNeuralDataSet(final int inputCount,
			final int idealCount) {
		this.inputCount = inputCount;
		this.idealCount = idealCount;
		this.dataset = new BasicNeuralDataSet();
	}

	public NormalizationTargetNeuralDataSet(final NeuralDataSet dataset) {
		this.dataset = dataset;
		this.inputCount = this.dataset.getInputSize();
		this.idealCount = this.dataset.getIdealSize();
	}

	public void close() {
	}

	public void open() {
	}

	public void write(final double[] data, final int inputCount) {

		if (this.idealCount == 0) {
			final BasicNeuralData inputData = new BasicNeuralData(data);
			this.dataset.add(inputData);
		} else {
			final BasicNeuralData inputData = new BasicNeuralData(
					this.inputCount);
			final BasicNeuralData idealData = new BasicNeuralData(
					this.idealCount);

			int index = 0;
			for (int i = 0; i < this.inputCount; i++) {
				inputData.setData(i, data[index++]);
			}

			for (int i = 0; i < this.idealCount; i++) {
				idealData.setData(i, data[index++]);
			}

			this.dataset.add(inputData, idealData);
		}

	}

}
