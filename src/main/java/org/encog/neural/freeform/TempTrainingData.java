/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.freeform;

/**
 * Some training methods require that temp data be stored during the training
 * process.
 */
public interface TempTrainingData {
	
	/**
	 * Add to the specified temp value.
	 * @param i The index.
	 * @param value The value to add.
	 */
	void addTempTraining(int i, double value);

	/**
	 * Allocate the specified length of temp training.
	 * @param l The length.
	 */
	void allocateTempTraining(int l);

	/**
	 * Clear the temp training.
	 */
	void clearTempTraining();

	/**
	 * Get the specified temp training.
	 * @param index The indfex.
	 * @return The temp training value.
	 */
	double getTempTraining(int index);

	/**
	 * Set a temp training value.
	 * @param index The index.
	 * @param value The value.
	 */
	void setTempTraining(int index, double value);
}
