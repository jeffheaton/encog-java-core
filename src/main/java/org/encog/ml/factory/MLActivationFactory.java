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
package org.encog.ml.factory;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.plugin.EncogPluginBase;
import org.encog.plugin.EncogPluginService1;

public class MLActivationFactory {

	public static final String AF_BIPOLAR = "bipolar";
	public static final String AF_COMPETITIVE = "comp";
	public static final String AF_GAUSSIAN = "gauss";
	public static final String AF_LINEAR = "linear";
	public static final String AF_LOG = "log";
	public static final String AF_RAMP = "ramp";
	public static final String AF_SIGMOID = "sigmoid";
	public static final String AF_SIN = "sin";
	public static final String AF_SOFTMAX = "softmax";
	public static final String AF_STEP = "step";
	public static final String AF_TANH = "tanh";

	public ActivationFunction create(String fn) {
		
		for (EncogPluginBase plugin : Encog.getInstance().getPlugins()) {
			if (plugin instanceof EncogPluginService1) {
				ActivationFunction result = ((EncogPluginService1) plugin).createActivationFunction(fn);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}
