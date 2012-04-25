package org.encog.cloud.indicator.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.cloud.indicator.IndicatorFactory;
import org.encog.cloud.indicator.IndicatorListener;

public class DownloadIndicatorFactory implements IndicatorFactory {

	private final List<String> dataRequested = new ArrayList<String>();	
	private final List<String> outputList = new ArrayList<String>();
	private final File file;

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
	 * @return the outputList
	 */
	public List<String> getOutputList() {
		return outputList;
	}
	
	public void requestData(String str) {
		dataRequested.add(str);
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

	
	@Override
	public String getName() {
		return "Download";
	}

	@Override
	public IndicatorListener create() {
		DownloadIndicator ind = new DownloadIndicator(file);
		
		ind.getDataRequested().addAll(this.dataRequested);
		ind.getOutputList().addAll(this.dataRequested);
		
		return ind;
	}

}
