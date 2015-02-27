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
package org.encog.util.obj;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public final class ActivationUtil {
	public static String generateActivationFactory(String name, ActivationFunction af) {
		StringBuilder result = new StringBuilder();
		
		result.append(name.toUpperCase());
		
		if( af.getParams()!=null && af.getParams().length>0 ) {
			result.append('[');			
			NumberList.toList(CSVFormat.EG_FORMAT, result, af.getParams());			
			result.append(']');
		}
		
		return result.toString();
	}
}
