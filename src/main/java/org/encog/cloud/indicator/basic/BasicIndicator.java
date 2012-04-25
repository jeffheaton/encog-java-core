package org.encog.cloud.indicator.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.cloud.indicator.IndicatorListener;
import org.encog.cloud.indicator.server.IndicatorLink;

public abstract class BasicIndicator implements IndicatorListener {

	private final List<String> dataRequested = new ArrayList<String>();	
	private final List<String> outputList = new ArrayList<String>();
	private IndicatorLink link;

	/**
	 * @return the dataRequested
	 */
	public List<String> getDataRequested() {
		return dataRequested;
	}
		
	/**
	 * @return the outputList
	 */
	public List<String> getOutputList() {
		return outputList;
	}
	
	public void requestData(String str) {
		dataRequested.add(str);
	}
	
	public void notifyConnect(IndicatorLink theLink)
	{
		this.link = theLink;
		this.link.requestSignal(this.dataRequested);
	}
	
	public void defineOutput(String color, String outputType, String name) {
		StringBuilder line = new StringBuilder();
		line.append("\"");
		line.append(color);
		line.append("\".\"");
		line.append(outputType);
		line.append("\"");
		line.append(name);
		line.append("\"");
		outputList.add(line.toString());
	}
	


}
