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
package org.encog.app.analyst.csv;

import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * Holds input data and the CSV row for a cluster item.
 */
public class ClusterRow extends BasicMLDataPair {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -6542401157014650211L;

	/**
	 * The loaded row of data.
	 */
	 private final LoadedRow row;

	 /**
	  * Construct the cluster row.
	  * @param input The input data.
	  * @param theRow The CSV row.
	  */
	public ClusterRow(final double[] input, final LoadedRow theRow) {
		super(new BasicMLData(input));
		this.row = theRow;
	}

	/**
	 * @return the row
	 */
	public final LoadedRow getRow() {
		return this.row;
	}
}
