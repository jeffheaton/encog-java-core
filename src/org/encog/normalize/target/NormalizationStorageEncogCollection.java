/*
 * Encog(tm) Core v2.5 - Java Version
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

package org.encog.normalize.target;

import org.encog.normalize.DataNormalization;
import org.encog.persist.annotations.EGAttribute;

public class NormalizationStorageEncogCollection implements
		NormalizationStorage {

	@EGAttribute
	private String resourceName;
	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(DataNormalization norm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(double[] data, int inputCount) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	

}
