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
package org.encog.ml.data.cross;

import org.encog.ml.MLMethod;
import org.encog.ml.data.versatile.MatrixMLDataSet;

public class DataFold {
	private final MatrixMLDataSet training;
	private final MatrixMLDataSet validation;
	private double score;
	private MLMethod method;
	
	public DataFold(MatrixMLDataSet theTraining,MatrixMLDataSet theValidation) {
		this.training = theTraining;
		this.validation = theValidation;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the training
	 */
	public MatrixMLDataSet getTraining() {
		return training;
	}

	/**
	 * @return the validation
	 */
	public MatrixMLDataSet getValidation() {
		return validation;
	}

	/**
	 * @return the method
	 */
	public MLMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(MLMethod method) {
		this.method = method;
	}
	
	
	
}
