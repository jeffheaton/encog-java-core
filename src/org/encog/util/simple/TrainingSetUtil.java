package org.encog.util.simple;

import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ObjectPair;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;

public class TrainingSetUtil {

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
		
		return new ObjectPair<double[][],double[][]>(a,b);
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
