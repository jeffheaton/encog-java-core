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
package org.encog.util.normalize.segregate;

import java.io.Serializable;

import org.encog.util.normalize.DataNormalization;

/**
 * Segregators are used to exclude certain rows. You may want to exclude rows to
 * create training and validation sets. You may also simply wish to exclude some
 * rows because they do not apply to what you are currently training for.
 */
public interface Segregator extends Serializable {
	
	/**
	 * @return The normalization object that is being used with this segregator.
	 */
	DataNormalization getNormalization();

	/**
	 * Setup this object to use the specified normalization object.
	 * @param normalization THe normalization object to use.
	 */
	void init(DataNormalization normalization);

	/**
	 * Should this row be included, according to this segregator.
	 * @return True if this row should be included.
	 */
	boolean shouldInclude();
	
	/**
	 * Init for a pass.
	 */
	void passInit();
}
