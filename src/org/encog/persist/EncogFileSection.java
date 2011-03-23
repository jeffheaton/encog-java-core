package org.encog.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystError;

public class EncogFileSection {
	
	private final String sectionName;
	private final String subSectionName;
	private final List<String> lines = new ArrayList<String>();
	
	public EncogFileSection(String sectionName, String subSectionName) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
	}
	
	public String getSectionName() {
		return sectionName;
	}
	
	public String getSubSectionName() {
		return subSectionName;
	}
	
	public List<String> getLines() {
		return lines;
	}

	public Map<String, String> parseParams() {
		Map<String,String> result = new HashMap<String,String>();

		for(String line: this.lines) {
			line = line.trim();
			if(line.length()>0 ) {
				int idx = line.indexOf('=');
				if( idx==-1 ) {
					throw new AnalystError("Invalid setup item: " + line);
				}
				String name = line.substring(0,idx).trim();
				String value = line.substring(idx+1).trim();
								
				result.put(name, value);
			}
		}
		
		return result;
	}
	
	
	
	
}
