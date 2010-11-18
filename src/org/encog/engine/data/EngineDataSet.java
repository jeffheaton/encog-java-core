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
package org.encog.engine.data;


/**
 * An interface designed to abstract classes that store machine learning data.
 * This interface is designed to provide EngineDataSet objects. These can be
 * used to train neural networks using both supervised and unsupervised
 * training.
 * 
 * Some implementations of this interface are memory based. That is they store
 * the entire contents of the dataset in memory.
 * 
 * Other implementations of this interface are not memory based. These
 * implementations read in data as it is needed. This allows very large datasets
 * to be used. Typically the add methods are not supported on non-memory based
 * datasets.
 * 
 * @author jheaton
 */
public interface EngineDataSet {

	/**
	 * @return The size of the input data.
	 */
	int getIdealSize();

	/**
	 * @return The size of the input data.
	 */
	int getInputSize();

	/**
	 * @return True if this is a supervised training set.
	 */
	boolean isSupervised();
}
