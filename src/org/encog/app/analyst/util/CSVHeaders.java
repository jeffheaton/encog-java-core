package org.encog.app.analyst.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class CSVHeaders {
	
	private List<String> headerList = new ArrayList<String>();
	
	public CSVHeaders(File filename, boolean headers, CSVFormat format) {
		ReadCSV csv = null;
		try {
			csv = new ReadCSV(filename.toString(), headers, format);
			if (csv.next()) {
				if( headers ) {
					for(int i=0;i<csv.getColumnCount();i++) {
						headerList.add(csv.getColumnNames().get(i));
					}
				} else {
					for(int i=0;i<csv.getColumnCount();i++) {
						headerList.add("field:"+(i+1));
					}
				}
			}
		} finally {
			if (csv != null)
				csv.close();
		}
	}
	
	public int size() {
		return this.headerList.size();
	}
	
	public List<String> getHeaders() {
		return this.headerList;
	}
	
	public String getHeader(int index) {
		return this.headerList.get(index);
	}
	
	public boolean isSeriesInput(int index) {
		String field = getHeader(index);
		return ( field.toLowerCase().startsWith("input:"));
	}
	
	public boolean isSeriesPredict(int index) {
		String field = getHeader(index);				
		return( field.toLowerCase().startsWith("predict:"));
	}
	
	public boolean isSeries(int index) {
		return isSeriesInput(index) || isSeriesPredict(index);
	}
	
	public String getBaseHeader(int index) { 
		String result = this.headerList.get(index);
		
		int loc = result.indexOf('-');
		if( loc!=-1 ) {
			result = result.substring(0,loc);
		}
		
		loc = result.indexOf('(');
		if( loc!=-1 ) {
			result = result.substring(0,loc);
		}
		
		if( result.toLowerCase().startsWith("input:")) {
			result = result.substring(6);
		}
		
		if( result.toLowerCase().startsWith("predict:")) {
			result = result.substring(8);
		}
		
		return result;
	}
}
