package org.encog.persist;

import java.util.ArrayList;
import java.util.List;

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
	
	
	
	
}
