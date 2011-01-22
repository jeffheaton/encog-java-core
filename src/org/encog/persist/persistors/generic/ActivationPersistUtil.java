/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.persist.persistors.generic;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.NeuralNetworkError;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.util.csv.CSVFormat;
import org.encog.util.obj.ReflectionUtil;

public class ActivationPersistUtil {
	public static void saveActivationFunction(
			ActivationFunction activationFunction, WriteXML out) {
		if (activationFunction != null) {
			
			String[] names = activationFunction.getParamNames();
			for (int i = 0; i < names.length; i++) {
				String str = names[i];
				double d = activationFunction.getParams()[i];
				out.addAttribute(str, "" + CSVFormat.EG_FORMAT.format(d, 10));
			}
			out.beginTag(activationFunction.getClass().getSimpleName());
			out.endTag();
		}
	}

	public static ActivationFunction loadActivation(String type, ReadXML in) {

		try {
			Class<?> clazz = ReflectionUtil.resolveEncogClass(type);
			
			if( clazz==null ) {
				throw new NeuralNetworkError("Unknown activation function type: " + type);
			}
			
			ActivationFunction result = (ActivationFunction) clazz
					.newInstance();

			for (String key : in.getTag().getAttributes().keySet()) {
				int index = -1;

				for (int i = 0; i < result.getParamNames().length; i++) {
					if (key.equalsIgnoreCase(result.getParamNames()[i])) {
						index = i;
						break;
					}					
				}
				
				if (index != -1) {
					String str = in.getTag().getAttributeValue(key);
					double d = CSVFormat.EG_FORMAT.parse(str);
					result.setParam(index, d);
				}
			}

			return result;
		} catch (InstantiationException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

}
