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
package org.encog.ensemble;

import org.encog.ml.MLMethod;

public interface EnsembleMLMethodFactory {

	public MLMethod createML(int inputs, int outputs);
	public void reInit(MLMethod ml);
	public String getLabel();
	/*
	 * This may be needed in some cases where we want to increase the size of the learner's memory
	 * (e.g more neurons for bigger aggregation)
	 */
	public void setSizeMultiplier(int sizeMultiplier);
}