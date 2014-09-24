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
package org.encog.ml.data.auto;

import org.encog.Encog;
import org.encog.util.Format;

public class AutoFloatColumn {
	private float[] data;
	private float actualMax;
	private float actualMin;
	
	public AutoFloatColumn(float[] theData) {
		this(theData,0,0);
		autoMinMax();
	}
	public AutoFloatColumn(float[] theData, float actualMax, float actualMin) {
		this.data = theData;
		this.actualMax = actualMax;
		this.actualMin = actualMin;
	}
	public void autoMinMax() {
		this.actualMax = Float.MIN_VALUE;
		this.actualMin = Float.MAX_VALUE;
		for(float f: this.data) {
			this.actualMax = Math.max(this.actualMax, f);
			this.actualMin = Math.min(this.actualMin, f);
		}
	}
	public float[] getData() {
		return data;
	}
	public float getActualMax() {
		return actualMax;
	}
	public float getActualMin() {
		return actualMin;
	}
	public float getNormalized(int index, float normalizedMin, float normalizedMax) {
		float x = data[index];
		return ((x - actualMin) 
				/ (actualMax - actualMin))
				* (normalizedMax - normalizedMin) + normalizedMin;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":min=");
		result.append(Format.formatDouble(this.actualMin, Encog.DEFAULT_PRECISION));
		result.append(",max=");
		result.append(Format.formatDouble(this.actualMin, Encog.DEFAULT_PRECISION));
		result.append(",max=");
		result.append("]");
		return result.toString();
	}
}
