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

import java.util.ArrayList;
import java.util.List;

import org.encog.cloud.indicator.IndicatorListener;
import org.encog.cloud.indicator.server.IndicatorLink;

/**
 * This abstract class provides low-level functionality to all Encog based
 * indicators.
 */
public abstract class BasicIndicator implements IndicatorListener {

	/**
	 * Is this indicator blocking, should it wait for a result after each bar.
	 */
	private final boolean blocking;
	
	/**
	 * The data that has been requested from the remote side.  This is
	 * typically HLOC(High, Low, Open, Close) data that is needed by the
	 * Encog indicator to compute.
	 */
	private final List<String> dataRequested = new ArrayList<String>();
	
	/**
	 * The number of bars requested per data item.
	 */
	private final List<Integer> dataCount = new ArrayList<Integer>();
	
	/**
	 * The communication link between the indicator and remote.
	 */
	private IndicatorLink link;
	
	/**
	 * The current error message;
	 */
	private String errorMessage;
	
	public BasicIndicator(boolean theBlocking) {
		this.blocking = theBlocking;
	}

	/**
	 * @return the dataRequested
	 */
	public List<String> getDataRequested() {
		return dataRequested;
	}
		
	/**
	 * Request the specified data. i.e. HIGH, LOW, etc.
	 * @param str The data being requested.
	 */
	public void requestData(String str) {
		dataRequested.add(str);
		
		int idx = str.indexOf('[');
		
		if( idx==-1 )
		{
			this.dataCount.add(1);
			return;
		}
					
		int idx2 = str.indexOf(']',idx);
					
		if( idx2==-1 )
		{
			this.dataCount.add(1);
			return;
		}
		
		String s = str.substring(idx+1,idx2);
		this.dataCount.add(Integer.parseInt(s));		
	}
	
	/**
	 * Notify that this indicator is now connected.  This is called
	 * once, at the beginning of a connection.  Indicators are not
	 * reused. 
	 */
	public void notifyConnect(IndicatorLink theLink)
	{
		this.link = theLink;
		if( this.errorMessage!=null ) {
			String[] args = { this.errorMessage };
			this.getLink().writePacket(IndicatorLink.PACKET_ERROR, args);
		} else {
			this.link.initConnection(this.dataRequested, this.blocking);
		}
	}

	/**
	 * @return the blocking
	 */
	public boolean isBlocking() {
		return blocking;
	}

	/**
	 * @return the link
	 */
	public IndicatorLink getLink() {
		return link;
	}

	/**
	 * @return the dataCount
	 */
	public List<Integer> getDataCount() {
		return dataCount;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
}
