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
package org.encog.util.datastruct;

import org.encog.util.EngineArray;

public class WindowInt {
	private int[] data;
	
	public WindowInt(int size) {
		this.data = new int[size];
	}
	
	public int size() {
		return this.data.length;
	}
	
	public void shift() {
		EngineArray.arrayCopy(this.data, 0, this.data, 1, size()-1);
	}
	
	public void add(int i) {
		shift();
		data[0] = i;
	}
	
	public int[] getData() {
		return this.data;
	}
}
