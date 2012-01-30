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
package org.encog.util.arrayutil;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Produce a time-series from an array.
 */
public class TemporalWindowArray {

	/**
	 * The size of the input window.
	 */
	private int inputWindow;

	/**
	 * The size of the prediction window.
	 */
	private int predictWindow;

	/**
	 * The fields that are to be processed.
	 */
	private TemporalWindowField[] fields;

	/**
	 * Construct a time-series from an array.
	 * @param theInputWindow The size of the input window.
	 * @param thePredictWindow The size of the predict window.
	 */
	public TemporalWindowArray(final int theInputWindow, 
			final int thePredictWindow) {
		this.inputWindow = theInputWindow;
		this.predictWindow = thePredictWindow;
	}

	/**
	 * Analyze the 1D array.
	 * @param array The array to analyze.
	 */
	public final void analyze(final double[] array) {
		this.fields = new TemporalWindowField[1];
		this.fields[0] = new TemporalWindowField("0");
		this.fields[0].setAction(TemporalType.InputAndPredict);
	}

	/**
	 * Analyze the 2D array.
	 * @param array The 2D array to analyze.
	 */
	public final void analyze(final double[][] array) {
		final int length = array[0].length;
		this.fields = new TemporalWindowField[length];
		for (int i = 0; i < length; i++) {
			this.fields[i] = new TemporalWindowField("" + i);
			this.fields[i].setAction(TemporalType.InputAndPredict);
		}
	}

	/**
	 * Count the number of input fields, or fields used to predict.
	 * 
	 * @return The number of input fields.
	 */
	public final int countInputFields() {
		int result = 0;

		for (final TemporalWindowField field : this.fields) {
			if (field.getInput()) {
				result++;
			}
		}

		return result;
	}

	/**
	 * Count the number of fields that are that are in the prediction.
	 * 
	 * @return The number of fields predicted.
	 */
	public final int countPredictFields() {
		int result = 0;

		for (final TemporalWindowField field : this.fields) {
			if (field.getPredict()) {
				result++;
			}
		}

		return result;
	}

	/**
	 * @return The fields that are to be processed.
	 */
	public final TemporalWindowField[] getFields() {
		return this.fields;
	}

	/**
	 * @return the inputWindow
	 */
	public final int getInputWindow() {
		return this.inputWindow;
	}

	/**
	 * @return the predictWindow
	 */
	public final int getPredictWindow() {
		return this.predictWindow;
	}

	/**
	 * Process the array.
	 * @param data The array to process.
	 * @return A neural data set that contains the time-series.
	 */
	public final MLDataSet process(final double[] data) {
		final MLDataSet result = new BasicMLDataSet();

		final int totalWindowSize = this.inputWindow + this.predictWindow;
		final int stopPoint = data.length - totalWindowSize;

		for (int i = 0; i < stopPoint; i++) {
			final MLData inputData 
				= new BasicMLData(this.inputWindow);
			final MLData idealData 
				= new BasicMLData(this.predictWindow);

			int index = i;

			// handle input window
			for (int j = 0; j < this.inputWindow; j++) {
				inputData.setData(j, data[index++]);
			}

			// handle predict window
			for (int j = 0; j < this.predictWindow; j++) {
				idealData.setData(j, data[index++]);
			}

			final MLDataPair pair = new BasicMLDataPair(inputData,
					idealData);
			result.add(pair);
		}

		return result;
	}

	/**
	 * @param theInputWindow
	 *            the inputWindow to set
	 */
	public final void setInputWindow(final int theInputWindow) {
		this.inputWindow = theInputWindow;
	}

	/**
	 * @param thePredictWindow
	 *            the predictWindow to set
	 */
	public final void setPredictWindow(final int thePredictWindow) {
		this.predictWindow = thePredictWindow;
	}
		
    /**
     * Processes the specified data array in an IMLDataset.
     * You can send a [][] array directly with this method.
     * @param data The data.
     * @return The data set.
     */
    public MLDataSet process(double[][] data)
    {
        MLDataSet result = new BasicMLDataSet();
        for(double[] doubles : data)
        {
            result.add(processToPair(doubles));
        }
        return result;
    }
	
    /**
     * Process the data array and returns an MLdatapair.
     * @param data The array to process.
     * @return MLDatapair containing data.
     */
    public MLDataPair processToPair(double[] data)
    {

        MLDataPair pair = null;
        int totalWindowSize = inputWindow + predictWindow;
        int stopPoint = data.length - totalWindowSize;

        for (int i = 0; i < stopPoint; i++)
        {
            MLData inputData = new BasicMLData(inputWindow);
            MLData idealData = new BasicMLData(predictWindow);

            int index = i;

            // handle input window
            for (int j = 0; j < inputWindow; j++)
            {
                inputData.setData(j, data[index++]);
            }

            // handle predict window
            for (int j = 0; j < predictWindow; j++)
            {
                idealData.setData(j, data[index++]);
            }

            pair = new BasicMLDataPair(inputData, idealData);
        }
        return pair;
    }

}
