package org.encog.app.analyst.script;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class EncogAnalystConfig {
	
	public final static String FILE_RAW = "FILE_RAW";
	public static final String FILE_NORMALIZE = "FILE_NORMALIZE";
	
	private Map<String,String> filenames = new HashMap<String,String>();
	private int maxClassSize = 50;
	private boolean allowIntClasses = true;
	private boolean allowRealClasses = false;
	private boolean allowStringClasses = true;
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
	 * @return the filenames
	 */
	public Map<String, String> getFilenames() {
		return filenames;
	}
	public void setFilename(String key, String value) {
		this.filenames.put(key, value);
		
	}
	public String getFilename(String sourceFile) {
		return this.filenames.get(sourceFile);
	}
	
	
}
