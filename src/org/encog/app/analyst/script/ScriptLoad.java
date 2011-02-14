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
import java.util.StringTokenizer;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.normalize.NormalizedField;
import org.encog.app.quant.normalize.NormalizationDesired;

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
	
	private List<String> splitColumns(String line) {
		List<String> result = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(line,",");
		while(tok.hasMoreTokens()) {
			String str = tok.nextToken().trim();
			if( str.length()>0 && str.charAt(0)=='\"')
			{
				str = str.substring(1);
				if( str.endsWith("\""))
				{
					str = str.substring(0,str.length()-1);
				}
			}
			result.add(str);
		}
		return result;
	}
	
	private void handleDataStats(List<String> list) {
		List<DataField> dfs = new ArrayList<DataField>();
		boolean first = true;
		for(String line: list) {
			if(!first ) {
				List<String> cols = splitColumns(line);
				String name = cols.get(0);
				boolean isclass = Integer.parseInt(cols.get(1))>0;
				boolean iscomplete = Integer.parseInt(cols.get(2))>0;
				boolean isint = Integer.parseInt(cols.get(3))>0;
				boolean isreal = Integer.parseInt(cols.get(4))>0;
				double amax = Double.parseDouble(cols.get(5));
				double amin = Double.parseDouble(cols.get(6));
				double mean = Double.parseDouble(cols.get(7));
				double sdev = Double.parseDouble(cols.get(8));
				DataField df = new DataField(name);
				df.setClass(isclass);
				df.setComplete(iscomplete);
				df.setInteger(isint);
				df.setReal(isreal);
				df.setMax(amax);
				df.setMin(amin);
				df.setMean(mean);
				df.setStandardDeviation(sdev);
				dfs.add(df);
			} else {
				first = false;
			}			
		}
		
		DataField[] array = new DataField[dfs.size()];
		for(int i=0;i<array.length;i++) {
			array[i] = dfs.get(i);
		}
		
		this.script.setFields(array);
	}
	
	private void handleNormalizeRange(List<String> list) {
		List<NormalizedField> nfs = new ArrayList<NormalizedField>();
		boolean first = true;
		for(String line: list) {
			if(!first ) {
				List<String> cols = splitColumns(line);
				String name = cols.get(0);				
				String action = cols.get(1);
				double high = Double.parseDouble(cols.get(2));
				double low = Double.parseDouble(cols.get(3));
				
				NormalizationDesired des = null;
				if( action.equals("range")) {
					des = NormalizationDesired.Normalize;
				} else if( action.equals("ignore")) {
					des = NormalizationDesired.Ignore;
				} else if( action.equals("pass")) {
					des = NormalizationDesired.PassThrough;
				}
				NormalizedField nf = new NormalizedField(name,des,high,low);
				nfs.add(nf);
			} else {
				first = false;
			}			
		}
		
		NormalizedField[] array = new NormalizedField[nfs.size()];
		for(int i=0;i<array.length;i++) {
			array[i] = nfs.get(i);
		}
		
		this.script.getNormalize().setNormalizedFields(array);

	}
	
	private void handleNormalizeConfig(List<String> list) {
		Map<String, String> prop = this.handleProperties(list);
		
		this.script.getNormalize().setSourceFile(prop.get("sourceFile"));
		this.script.getNormalize().setTargetFile(prop.get("targetFile"));
	}
	
	private void processSubSection(String currentSection, String currentSubsection, List<String> list)
	{
		if( currentSection.equals("SETUP") && currentSubsection.equalsIgnoreCase("CONFIG") ) {
			handleConfig(list);
		} else if( currentSection.equals("SETUP") && currentSubsection.equalsIgnoreCase("FILENAMES") ) {
			handleFilenames(list);
		} else if( currentSection.equals("DATA") && currentSubsection.equalsIgnoreCase("STATS") ) {
			handleDataStats(list);
		} else if( currentSection.equals("NORMALIZE") && currentSubsection.equalsIgnoreCase("RANGE") ) {
			handleNormalizeRange(list);
		} else if( currentSection.equals("NORMALIZE") && currentSubsection.equalsIgnoreCase("CONFIG") ) {
			handleNormalizeConfig(list);
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
