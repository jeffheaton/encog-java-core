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

/**
 * Operations that the temporal class may perform on fields.
 */
public enum TemporalType {
	/**
	 * This field is used as part of the input. However, if you wish to use the
	 * field for prediction as well, specify InputAndPredict.
	 */
	Input,

	/**
	 * This field is used as part of the prediction. However, if you wish to use
	 * the field for input as well, specify InputAndPredict.
	 */
	Predict,

	/**
	 * This field is used for both input and prediction.
	 */
	InputAndPredict,

	/**
	 * This field should be ignored.
	 */
	Ignore,

	/**
	 * This field should pass through, to the output file, without modification.
	 */
	PassThrough
}
