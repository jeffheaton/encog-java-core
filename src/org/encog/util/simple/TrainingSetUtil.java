/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.simple;

import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ObjectPair;
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
		int length = (int)training.getRecordCount();
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
}
