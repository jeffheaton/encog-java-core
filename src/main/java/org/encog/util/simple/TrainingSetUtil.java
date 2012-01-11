/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.EngineArray;
import org.encog.util.ObjectPair;
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
	public static MLDataSet loadCSVTOMemory(CSVFormat format,
			String filename, boolean headers, int inputSize, int idealSize) {
		MLDataSet result = new BasicMLDataSet();
		ReadCSV csv = new ReadCSV(filename, headers, format);
		while (csv.next()) {
			MLData input = null;
			MLData ideal = null;
			int index = 0;

			input = new BasicMLData(inputSize);
			for (int i = 0; i < inputSize; i++) {
				double d = csv.getDouble(index++);
				input.setData(i, d);
			}

			if (idealSize > 0) {
				ideal = new BasicMLData(idealSize);
				for (int i = 0; i < idealSize; i++) {
					double d = csv.getDouble(index++);
					ideal.setData(i, d);
				}
			}

			MLDataPair pair = new BasicMLDataPair(input, ideal);
			result.add(pair);
		}

		return result;
	}

	public static ObjectPair<double[][], double[][]> trainingToArray(
			MLDataSet training) {
		int length = (int)training.getRecordCount();
		double[][] a = new double[length][training.getInputSize()];
		double[][] b = new double[length][training.getIdealSize()];

		int index = 0;
		for (MLDataPair pair : training) {
			EngineArray.arrayCopy(pair.getInputArray(), a[index]);
			EngineArray.arrayCopy(pair.getIdealArray(), b[index]);
			index++;
		}

		return new ObjectPair<double[][], double[][]>(a, b);
	}
}
