package org.encog.app.analyst.script;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.encog.app.analyst.AnalystError;
import org.encog.util.csv.CSVFormat;

public class EncogAnalystConfig {
	
	private int maxClassSize = 50;
	private boolean allowIntClasses = true;
	private boolean allowRealClasses = false;
	private boolean allowStringClasses = true;
	private boolean outputHeaders = true;
	private boolean inputHeaders = false;
	private CSVFormat csvFormat = CSVFormat.ENGLISH;
	/**
	 * @return the maxClassSize
	 */
	public int getMaxClassSize() {
		return maxClassSize;
	}
	/**
	 * @param maxClassSize the maxClassSize to set
	 */
	public void setMaxClassSize(int maxClassSize) {
		this.maxClassSize = maxClassSize;
	}
	/**
	 * @return the allowIntClasses
	 */
	public boolean isAllowIntClasses() {
		return allowIntClasses;
	}
	/**
	 * @param allowIntClasses the allowIntClasses to set
	 */
	public void setAllowIntClasses(boolean allowIntClasses) {
		this.allowIntClasses = allowIntClasses;
	}
	/**
	 * @return the allowRealClasses
	 */
	public boolean isAllowRealClasses() {
		return allowRealClasses;
	}
	/**
	 * @param allowRealClasses the allowRealClasses to set
	 */
	public void setAllowRealClasses(boolean allowRealClasses) {
		this.allowRealClasses = allowRealClasses;
	}
	/**
	 * @return the allowStringClasses
	 */
	public boolean isAllowStringClasses() {
		return allowStringClasses;
	}
	/**
	 * @param allowStringClasses the allowStringClasses to set
	 */
	public void setAllowStringClasses(boolean allowStringClasses) {
		this.allowStringClasses = allowStringClasses;
	}
	public String getAllowedClasses() {
		StringBuilder result = new StringBuilder();
		
		if( this.allowIntClasses ) {
			if( result.length()!=0 )
				result.append(',');
			result.append("integer");
		}
		
		if( this.allowRealClasses ) {
			if( result.length()!=0 )
				result.append(',');
			result.append("real");
		}
		
		if( this.allowStringClasses ) {
			if( result.length()!=0 )
				result.append(',');
			result.append("string");
		}
		
		return result.toString();
	}
	
	public void setAllowedClasses(String str)
	{
		this.allowIntClasses = false;
		this.allowStringClasses = false;
		this.allowRealClasses = false;
		StringTokenizer tok = new StringTokenizer(str,",");
		while(tok.hasMoreTokens())
		{
			String s = tok.nextToken();
			if( s.equals("integer") )
			{
				this.allowIntClasses = true;
			}
			else if( s.equals("real") )
			{
				this.allowRealClasses = true;
			}
			else if( s.equals("string") )
			{
				this.allowStringClasses = true;
			}
		}
	}
	
	/**
	 * @return the outputHeaders
	 */
	public boolean isOutputHeaders() {
		return outputHeaders;
	}
	/**
	 * @param outputHeaders the outputHeaders to set
	 */
	public void setOutputHeaders(boolean outputHeaders) {
		this.outputHeaders = outputHeaders;
	}
	/**
	 * @return the csvFormat
	 */
	public CSVFormat getCSVFormat() {
		return csvFormat;
	}
	/**
	 * @param csvFormat the csvFormat to set
	 */
	public void setCSVFormat(CSVFormat csvFormat) {
		this.csvFormat = csvFormat;
	}
	public void setCSVFormat(String value) {
		if( value.equals("deccomma") ) {
			this.csvFormat = CSVFormat.DECIMAL_COMMA;
		} else {
			this.csvFormat = CSVFormat.DECIMAL_POINT;
		}
			
		
	}
	/**
	 * @return the inputHeaders
	 */
	public boolean isInputHeaders() {
		return inputHeaders;
	}
	/**
	 * @param inputHeaders the inputHeaders to set
	 */
	public void setInputHeaders(boolean inputHeaders) {
		this.inputHeaders = inputHeaders;
	}
	
	
	
	
	
	
}
