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
package org.encog.ml.factory.parse;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the parse results for one layer.
 */
public class ArchitectureLayer {
	
	/**
	 * True/false if bias is present.
	 */
	private boolean bias;
	
	/**
	 * The count.
	 */
	private int count;
	
	/**
	 * The name of this layer.
	 */
	private String name;
	
	/**
	 * True, if default counts should be used.
	 */
	private boolean usedDefault;
	
	/**
	 * Holds any paramaters that were specified for the layer.
	 */
	private final Map<String, String> params = new HashMap<String, String>();

	/**
	 * @return the count.
	 */
	public final int getCount() {
		return this.count;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return the params
	 */
	public final Map<String, String> getParams() {
		return this.params;
	}

	/**
	 * @return the bias
	 */
	public final boolean isBias() {
		return this.bias;
	}

	/**
	 * @return the usedDefault
	 */
	public final boolean isUsedDefault() {
		return this.usedDefault;
	}

	/**
	 * @param theBias
	 *            the bias to set
	 */
	public final void setBias(final boolean theBias) {
		this.bias = theBias;
	}

	/**
	 * @param theCount
	 *            the count to set
	 */
	public final void setCount(final int theCount) {
		this.count = theCount;
	}

	/**
	 * @param theName
	 *            the name to set
	 */
	public final void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * @param theUsedDefault
	 *            the usedDefault to set
	 */
	public final void setUsedDefault(final boolean theUsedDefault) {
		this.usedDefault = theUsedDefault;
	}

}
