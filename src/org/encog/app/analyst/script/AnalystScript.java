package org.encog.app.analyst.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.analyze.AnalyzedField;

public class AnalystScript {

	private final EncogAnalystConfig config = new EncogAnalystConfig();
	private AnalyzedField[] fields;

	/**
	 * @return the config
	 */
	public EncogAnalystConfig getConfig() {
		return config;
	}

	/**
	 * @return the fields
	 */
	public AnalyzedField[] getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(AnalyzedField[] fields) {
		this.fields = fields;
	}

	private void saveConfig(WriteScriptFile out) {
		out.addSection("SETUP");
		out.addSubSection("CONFIG");
		out.writeProperty("maxClassCount", this.config.getMaxClassSize());
	}

	private void saveData(WriteScriptFile out) {
		out.addSection("DATA");
		out.addSubSection("STATS");
		out.addColumn("name");
		out.addColumn("isclass");
		out.addColumn("iscomplete");
		out.addColumn("isint");
		out.addColumn("isreal");
		out.addColumn("amax");
		out.addColumn("amin");
		out.addColumn("mean");
		out.addColumn("sdev");
		out.writeLine();

		for (AnalyzedField field : this.fields) {
			out.addColumn(field.getName());
			out.addColumn(field.isClass());
			out.addColumn(field.isComplete());
			out.addColumn(field.isInteger());
			out.addColumn(field.isReal());
			out.addColumn(field.getMax());
			out.addColumn(field.getMin());
			out.addColumn(field.getMean());
			out.addColumn(field.getStandardDeviation());
			out.writeLine();
		}
		out.flush();

		out.addSubSection("CLASSES");
		for (AnalyzedField field : this.fields) {
			if (field.isClass()) {
				out.addColumn(field.getName());
				out.addColumns(field.getClassMembers());
				out.writeLine();
			}
		}

	}

	public void save(OutputStream stream) {
		WriteScriptFile out = new WriteScriptFile(stream);
		saveConfig(out);
		saveData(out);
		out.flush();
	}
	
	private void handleConfig(List<String> list) {
		for(String line: list) {
			line = line.trim();
			if(line.length()>0 ) {
				int idx = line.indexOf('=');
				if( idx==-1 ) {
					throw new AnalystError("Invalid setup item: " + line);
				}
				String name = line.substring(0,idx);
				String value = line.substring(idx+1);
				
				if( name.equals("maxClassCount")) {
					this.config.setMaxClassSize(Integer.parseInt(value));
				}
				else {
					throw new AnalystError("Invalid setup item: " + line);
				}
			}
		}
	}
	
	private void processSubSection(String currentSection, String currentSubsection, List<String> list)
	{
		if( currentSection.equals("SETUP") && currentSubsection.equalsIgnoreCase("CONFIG") ) {
			handleConfig(list);
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
