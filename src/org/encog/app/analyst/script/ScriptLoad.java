package org.encog.app.analyst.script;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.PropertyConstraints;
import org.encog.app.analyst.script.prop.PropertyEntry;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogReadHelper;

/**
 * Used to load an Encog Analyst script.
 *
 */
public class ScriptLoad {

	private AnalystScript script;

	public ScriptLoad(AnalystScript script) {
		this.script = script;
	}

	private void handleFilenames(EncogFileSection section) {

		Map<String, String> prop = section.parseParams();
		this.script.getProperties().clearFilenames();

		for (Entry<String, String> e : prop.entrySet()) {
			this.script.getProperties().setFilename(e.getKey(), e.getValue());
		}
	}

	private void handleDataStats(EncogFileSection section) {
		List<DataField> dfs = new ArrayList<DataField>();
		boolean first = true;
		for (String line : section.getLines()) {
			if (!first) {
				List<String> cols = EncogFileSection.splitColumns(line);
				String name = cols.get(0);
				boolean isclass = Integer.parseInt(cols.get(1)) > 0;
				boolean iscomplete = Integer.parseInt(cols.get(2)) > 0;
				boolean isint = Integer.parseInt(cols.get(3)) > 0;
				boolean isreal = Integer.parseInt(cols.get(4)) > 0;
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
		for (int i = 0; i < array.length; i++) {
			array[i] = dfs.get(i);
		}

		this.script.setFields(array);
	}

	private void handleDataClasses(EncogFileSection section) {

		Map<String, List<AnalystClassItem>> map = new HashMap<String, List<AnalystClassItem>>();

		boolean first = true;
		for (String line : section.getLines()) {
			if (!first) {
				List<String> cols = EncogFileSection.splitColumns(line);
				
				if( cols.size()<4) {
					throw new AnalystError("Invalid data class: " + line);
				}
				
				String field = cols.get(0);
				String code = cols.get(1);
				String name = cols.get(2);
				int count = Integer.parseInt(cols.get(3));
				
				DataField df = this.script.findDataField(field);

				if (df == null) {
					throw new AnalystError(
							"Attempting to add class to unknown field: " + name);
				}

				List<AnalystClassItem> classItems;

				if (!map.containsKey(field)) {
					classItems = new ArrayList<AnalystClassItem>();
					map.put(field, classItems);
				} else {
					classItems = map.get(field);
				}

				classItems.add(new AnalystClassItem(code, name, count));
			} else {
				first = false;
			}
		}

		for (DataField field : this.script.getFields()) {
			if (field.isClass()) {
				List<AnalystClassItem> classList = map.get(field.getName());
				Collections.sort(classList);
				field.getClassMembers().clear();
				field.getClassMembers().addAll(classList);
			}
		}

	}

	private void handleNormalizeRange(EncogFileSection section) {
		List<AnalystField> nfs = new ArrayList<AnalystField>();
		boolean first = true;
		for (String line : section.getLines()) {
			if (!first) {
				List<String> cols = EncogFileSection.splitColumns(line);
				String name = cols.get(0);
				String action = cols.get(1);
				double high = Double.parseDouble(cols.get(2));
				double low = Double.parseDouble(cols.get(3));

				NormalizationAction des = null;
				if (action.equals("range")) {
					des = NormalizationAction.Normalize;
				} else if (action.equals("ignore")) {
					des = NormalizationAction.Ignore;
				} else if (action.equals("pass")) {
					des = NormalizationAction.PassThrough;
				} else if (action.equals("equilateral")) {
					des = NormalizationAction.Equilateral;
				} else if (action.equals("single")) {
					des = NormalizationAction.SingleField;
				} else if (action.equals("oneof")) {
					des = NormalizationAction.OneOf;
				}

				AnalystField nf = new AnalystField(name, des, high, low);
				nfs.add(nf);
			} else {
				first = false;
			}
		}

		AnalystField[] array = new AnalystField[nfs.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = nfs.get(i);
		}

		this.script.getNormalize().setNormalizedFields(array);
	}
	
	
	private void validateProperty(String section, String subSection, String name, String value) {
		PropertyEntry entry = PropertyConstraints.getInstance().getEntry(section,subSection,name);
		if( entry==null ) {
			throw new AnalystError("Unknown property: " + PropertyEntry.dotForm(section,subSection,name));
		}
		entry.validate(section,subSection,name,value);
	}

	private void loadSubSection(EncogFileSection section) {
		Map<String, String> prop = section.parseParams();

		for (String name : prop.keySet()) {
			String key = section.getSectionName().toUpperCase() + ":"
					+ section.getSubSectionName().toUpperCase() + "_" + name;
			String value = prop.get(name);
			if (value == null) {
				value = "";
			}
			validateProperty(section.getSectionName(),section.getSubSectionName(),name,value);
			this.script.getProperties().setProperty(key, value);
		}
	}

	private void handleSegregateFiles(EncogFileSection section) {
		List<AnalystSegregateTarget> nfs = new ArrayList<AnalystSegregateTarget>();
		boolean first = true;
		for (String line : section.getLines()) {
			if (!first) {
				List<String> cols = EncogFileSection.splitColumns(line);
				String filename = cols.get(0);
				int percent = Integer.parseInt(cols.get(1));

				AnalystSegregateTarget nf = new AnalystSegregateTarget(
						filename, percent);
				nfs.add(nf);
			} else {
				first = false;
			}
		}

		AnalystSegregateTarget[] array = new AnalystSegregateTarget[nfs.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = nfs.get(i);
		}

		this.script.getSegregate().setSegregateTargets(array);
	}

	private void processSubSection(EncogFileSection section) {
		String currentSection = section.getSectionName();
		String currentSubsection = section.getSubSectionName();

		if (currentSection.equals("SETUP")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SETUP")
				&& currentSubsection.equalsIgnoreCase("FILENAMES")) {
			handleFilenames(section);
		} else if (currentSection.equals("DATA")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("DATA")
				&& currentSubsection.equalsIgnoreCase("STATS")) {
			handleDataStats(section);
		} else if (currentSection.equals("DATA")
				&& currentSubsection.equalsIgnoreCase("CLASSES")) {
			handleDataClasses(section);
		} else if (currentSection.equals("NORMALIZE")
				&& currentSubsection.equalsIgnoreCase("RANGE")) {
			handleNormalizeRange(section);
		} else if (currentSection.equals("NORMALIZE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("NORMALIZE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SERIES")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("RANDOMIZE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SEGREGATE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SEGREGATE")
				&& currentSubsection.equalsIgnoreCase("FILES")) {
			handleSegregateFiles(section);
		} else if (currentSection.equals("GENERATE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("HEADER")
				&& currentSubsection.equalsIgnoreCase("DATASOURCE")) {
			loadSubSection(section);
		} else if (currentSection.equals("ML")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("ML")
				&& currentSubsection.equalsIgnoreCase("TRAIN")) {
			loadSubSection(section);
		} else if (currentSection.equals("TASKS")
				&& currentSubsection.length() > 0) {
			handleTask(section);
		}  else if (currentSection.equals("BALANCE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		}
	}

	private void handleTask(EncogFileSection section) {
		AnalystTask task = new AnalystTask(section.getSubSectionName());
		for (String line : section.getLines()) {
			task.getLines().add(line);
		}
		this.script.addTask(task);
	}

	public void load(InputStream stream) {
		EncogReadHelper reader = null;

		try {
			EncogFileSection section = null;
			reader = new EncogReadHelper(stream);

			while ((section = reader.readNextSection()) != null) {
				this.processSubSection(section);
			}

			// init the script
			this.script.init();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
