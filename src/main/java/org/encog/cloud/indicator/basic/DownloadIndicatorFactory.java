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
package org.encog.cloud.indicator.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.cloud.indicator.IndicatorFactory;
import org.encog.cloud.indicator.IndicatorListener;

/**
 * A factory used to produce indicators of the type DownloadIndicator.
 * Make sure to specify the file to download to, as well as the
 * data requested from the remote.
 */
public class DownloadIndicatorFactory implements IndicatorFactory {

	/**
	 * The data requested.
	 */
	private final List<String> dataRequested = new ArrayList<String>();
	
	/**
	 * The file to download to.
	 */
	private final File file;

	/**
	 * Construct the factory.
	 * @param theFile The file to download to.
	 */
	public DownloadIndicatorFactory(File theFile)
	{
		this.file = theFile;
	}
	
	/**
	 * @return the dataRequested
	 */
	public List<String> getDataRequested() {
		return dataRequested;
	}	
	
	/**
	 * Request the specified item of data.
	 * @param str
	 */
	public void requestData(String str) {
		dataRequested.add(str);
	}
		
	/**
	 * @return The name of this indicator, which is "Download".
	 */
	@Override
	public String getName() {
		return "Download";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndicatorListener create() {
		DownloadIndicator ind = new DownloadIndicator(file);
		
		for(String d : this.dataRequested) {
			ind.requestData(d);
		}
		
		return ind;
	}

}
