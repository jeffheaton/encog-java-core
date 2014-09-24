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
package org.encog.util.arrayutil;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.util.EngineArray;

/**
 * Create a sliding window of double arrays. New vectors can be added to the
 * window. Once the required number of vectors have been added then the entire
 * window can be copied to an output vector large enough to hold it.
 */
public class VectorWindow {
	
	/**
	 * The window.
	 */
	private final List<double[]> window = new ArrayList<double[]>();
	
	/**
	 * The number of slices in a window.
	 */
	private final int sliceCount;

	/**
	 * Construct a sliding window.
	 * @param theSliceCount The number of slices in a window.
	 */
	public VectorWindow(int theSliceCount) {
		this.sliceCount = theSliceCount;
	}

	/**
	 * Add a single vector to the window.
	 * @param vec The vector to add to the window.
	 */
	public void add(double[] vec) {
		this.window.add(vec.clone());
		while (this.window.size() > this.sliceCount) {
			this.window.remove(0);
		}
	}

	/**
	 * @return True, if we've added enough slices for a complete window.
	 */
	public boolean isReady() {
		return this.window.size() >= this.sliceCount;
	}

	/**
	 * Copy the entire window to a complete vector.
	 * @param output The vector to copy to.
	 * @param startPos The starting position to write to.
	 */
	public void copyWindow(double[] output, int startPos) {
		if (!isReady()) {
			throw new EncogError("Can't produce a timeslice of size "
					+ this.sliceCount + ", there are only "
					+ this.window.size() + " vectors loaded.");
		}

		int currentIndex = startPos;
		for (int i = 0; i < this.window.size(); i++) {
			double[] source = this.window.get(i);
			EngineArray.arrayCopy(source, 0, output, currentIndex,
					source.length);
			currentIndex += source.length;
		}
	}
}
