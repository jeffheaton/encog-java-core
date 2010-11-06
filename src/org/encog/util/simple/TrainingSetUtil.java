package org.encog.util.simple;

import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ObjectPair;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class TrainingSetUtil {

	/**
	 * Load a CSV file into a memory dataset.  
	 * @param format The CSV format to use.
	 * @param filename The filename to load.
	 * @param headers True if there is a header line.
	 * @param inputSize The input size.  Input always comes first in a file.
	 * @param idealSize The ideal size, 0 for unsupervised.
	 * @return A NeuralDataSet that holds the contents of the CSV file.
	 */
	public static NeuralDataSet loadCSVTOMemory(CSVFormat format,
			String filename, boolean headers, int inputSize, int idealSize) {
		NeuralDataSet result = new BasicNeuralDataSet();
		ReadCSV csv = new ReadCSV(filename, headers, format);
		while (csv.next()) {
			NeuralData input = null;
			NeuralData ideal = null;
			int index = 0;

			input = new BasicNeuralData(inputSize);
			for (int i = 0; i < inputSize; i++) {
				double d = csv.getDouble(index++);
				input.setData(i, d);
			}

			if (inputSize > 0) {
				ideal = new BasicNeuralData(inputSize);
				for (int i = 0; i < idealSize; i++) {
					double d = csv.getDouble(index++);
					ideal.setData(i, d);
				}
			}

			NeuralDataPair pair = new BasicNeuralDataPair(input, ideal);
			result.add(pair);
		}

		return result;
	}

	public static ObjectPair<double[][], double[][]> trainingToArray(
			NeuralDataSet training) {
		int length = trainingSetSize(training);
		double[][] a = new double[length][training.getInputSize()];
		double[][] b = new double[length][training.getIdealSize()];

		int index = 0;
		for (NeuralDataPair pair : training) {
			EngineArray.arrayCopy(pair.getInputArray(), a[index]);
			EngineArray.arrayCopy(pair.getIdealArray(), b[index]);
			index++;
		}

		return new ObjectPair<double[][], double[][]>(a, b);
	}

	public static int trainingSetSize(NeuralDataSet training) {

		if (training instanceof Indexable) {
			return (int) ((Indexable) training).getRecordCount();
		} else {
			int length = 0;
			for (NeuralDataPair pair : training) {
				length++;
			}
			return length;
		}

	}
}
