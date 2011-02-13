package org.encog.app.analyst.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.app.analyst.AnalystError;

public class ScriptLoad {
	
	private AnalystScript script;
	
	public ScriptLoad(AnalystScript script) {
		this.script = script;
	}
	
	private Map<String,String> handleProperties(List<String> list)
	{
		Map<String,String> result = new HashMap<String,String>();
		
		for(String line: list) {
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

	private void handleConfig(List<String> list) {
		
		Map<String,String> prop = handleProperties(list);
		
		for(Entry<String, String> e : prop.entrySet() )
		{
			String name = e.getKey();
			String value = e.getValue();
			if( name.equals("maxClassCount")) {
				this.script.getConfig().setMaxClassSize(Integer.parseInt(value));
			}
			else if( name.equals("allowedClasses")) {
				this.script.getConfig().setAllowedClasses(value);
			} else {
				throw new AnalystError("Invalid setup item: " + name);
			}
		}
	}
	
	private void handleFilenames(List<String> list) {
		
		Map<String,String> prop = handleProperties(list);
		this.script.getConfig().getFilenames().clear();
		
		for(Entry<String, String> e : prop.entrySet() )
		{
			this.script.getConfig().setFilename(e.getKey(), e.getValue());
		}
	}
	
	private void processSubSection(String currentSection, String currentSubsection, List<String> list)
	{
		if( currentSection.equals("SETUP") && currentSubsection.equalsIgnoreCase("CONFIG") ) {
			handleConfig(list);
		} else if( currentSection.equals("SETUP") && currentSubsection.equalsIgnoreCase("FILENAMES") ) {
			handleFilenames(list);
		}
	}

	public void load(InputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			String line;
			List<String> subSection = new ArrayList<String>();
			String currentSection = "";
			String currentSubSection = "";

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				// is it a comment
				if (line.startsWith("//")) {
					continue;
				}

				// is it a section or subsection
				else if (line.startsWith("**")) {			
					// handle previous section
					this.processSubSection(currentSection, currentSubSection, subSection);
					
					// now begin the new section
					subSection.clear();
					line = line.substring(2).trim();
					int idx = line.indexOf(':');
					if (idx == -1) {
						currentSection = line;
					} else {
						if (currentSection.length() < 1) {
							throw new AnalystError(
									"Can't begin subsection when a section has not yet been defined: "
											+ line);
						}

						String newSection = line.substring(0, idx);
						String newSubSection = line.substring(idx + 1);

						if (!newSection.equals(currentSection)) {
							throw new AnalystError("Can't begin subsection "
									+ line
									+ ", while we are still in the section: "
									+ currentSection);
						}

						currentSubSection = newSubSection;
					}
				} else if( line.length()<1 ) {
					continue;
				}
				else {
					if (currentSection.length() <1) {
						throw new AnalystError(
								"Unknown command before first section: " + line);						
					}
					
					subSection.add(line);
				}
				
				
			}
			this.processSubSection(currentSection, currentSubSection, subSection);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		}
	}

}
