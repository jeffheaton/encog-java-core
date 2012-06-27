/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.app.analyst.script;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.PropertyConstraints;
import org.encog.app.analyst.script.prop.PropertyEntry;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.persist.EncogWriteHelper;

/**
 * Used to save an Encog Analyst script.
 * 
 */
public class ScriptSave {

	/**
	 * The script to save.
	 */
	private final AnalystScript script;

	/**
	 * Construct the script.
	 * 
	 * @param theScript
	 *            The script to save.
	 */
	public ScriptSave(final AnalystScript theScript) {
		this.script = theScript;
	}

	/**
	 * Save the script to a stream.
	 * 
	 * @param stream
	 *            The output stream.
	 */
	public final void save(final OutputStream stream) {
		final EncogWriteHelper out = new EncogWriteHelper(stream);
		saveSubSection(out, "HEADER", "DATASOURCE");
		saveConfig(out);

		if (this.script.getFields() != null) {
			saveData(out);
			saveNormalize(out);
		}

		saveSubSection(out, "RANDOMIZE", "CONFIG");
		saveSubSection(out, "CLUSTER", "CONFIG");
		saveSubSection(out, "BALANCE", "CONFIG");

		if (this.script.getSegregate().getSegregateTargets() != null) {
			saveSegregate(out);
		}
		saveSubSection(out, "GENERATE", "CONFIG");
		saveMachineLearning(out);
		saveTasks(out);
		out.flush();
	}

	/**
	 * Save the config info.
	 * 
	 * @param out
	 *            THe output file.
	 */
	private void saveConfig(final EncogWriteHelper out) {
		saveSubSection(out, "SETUP", "CONFIG");
		out.addSubSection("FILENAMES");

		final List<String> list = this.script.getProperties().getFilenames();

		for (final String key : list) {
			final String value = this.script.getProperties().getFilename(key);
			final File f = new File(value);
			if ((f.getParent() != null)
					&& f.getParent()
							.equalsIgnoreCase(this.script.getBasePath())) {
				out.writeProperty(key, f.getName());
			} else {
				out.writeProperty(key, value);
			}
		}
	}

	/**
	 * Save the data fields.
	 * 
	 * @param out
	 *            The output file.
	 */
	private void saveData(final EncogWriteHelper out) {
		saveSubSection(out, "DATA", "CONFIG");
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

		for (final DataField field : this.script.getFields()) {
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
		out.addColumn("field");
		out.addColumn("code");
		out.addColumn("name");
		out.addColumn("count");
		out.writeLine();

		for (final DataField field : this.script.getFields()) {
			if (field.isClass()) {
				for (final AnalystClassItem col : field.getClassMembers()) {
					out.addColumn(field.getName());
					out.addColumn(col.getCode());
					out.addColumn(col.getName());
					out.addColumn(col.getCount());
					out.writeLine();
				}
			}
		}

	}

	/**
	 * Save the ML sections.
	 * 
	 * @param out
	 *            The output file.
	 */
	private void saveMachineLearning(final EncogWriteHelper out) {
		saveSubSection(out, "ML", "CONFIG");
		saveSubSection(out, "ML", "TRAIN");

	}

	/**
	 * Save the normalization data.
	 * 
	 * @param out
	 *            The output file.
	 */
	private void saveNormalize(final EncogWriteHelper out) {
		saveSubSection(out, "NORMALIZE", "CONFIG");

		out.addSubSection("RANGE");
		out.addColumn("name");
		out.addColumn("io");
		out.addColumn("timeSlice");
		out.addColumn("action");
		out.addColumn("high");
		out.addColumn("low");
		out.writeLine();
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			out.addColumn(field.getName());
			if (field.isInput()) {
				out.addColumn("input");
			} else {
				out.addColumn("output");
			}
			out.addColumn(field.getTimeSlice());
			switch (field.getAction()) {
			case Ignore:
				out.addColumn("ignore");
				break;
			case Normalize:
				out.addColumn("range");
				break;
			case PassThrough:
				out.addColumn("pass");
				break;
			case OneOf:
				out.addColumn("oneof");
				break;
			case Equilateral:
				out.addColumn("equilateral");
				break;
			case SingleField:
				out.addColumn("single");
				break;
			default:
				throw new AnalystError("Unknown action: " + field.getAction());
			}

			out.addColumn(field.getNormalizedHigh());
			out.addColumn(field.getNormalizedLow());
			out.writeLine();
		}
	}

	/**
	 * Save segregate info.
	 * @param out The output file.
	 */
	private void saveSegregate(final EncogWriteHelper out) {
		saveSubSection(out, "SEGREGATE", "CONFIG");
		out.addSubSection("FILES");
		out.addColumn("file");
		out.addColumn("percent");
		out.writeLine();

		for (final AnalystSegregateTarget target : this.script.getSegregate()
				.getSegregateTargets()) {
			out.addColumn(target.getFile());
			out.addColumn(target.getPercent());
			out.writeLine();
		}
	}

	/**
	 * Save a subsection.
	 * @param out The output file.
	 * @param section The section.
	 * @param subSection The subsection.
	 */
	private void saveSubSection(final EncogWriteHelper out,
			final String section, final String subSection) {
		if (!section.equals(out.getCurrentSection())) {
			out.addSection(section);
		}
		out.addSubSection(subSection);
		final List<PropertyEntry> list = PropertyConstraints.getInstance()
				.getEntries(section, subSection);
		Collections.sort(list);
		for (final PropertyEntry entry : list) {
			final String key = section + ":" + subSection + "_"
					+ entry.getName();
			final String value = this.script.getProperties().getPropertyString(
					key);
			if (value != null) {
				out.writeProperty(entry.getName(), value);
			} else {
				out.writeProperty(entry.getName(), "");
			}
		}
	}

	/**
	 * Save the tasks.
	 * @param out The output file.
	 */
	private void saveTasks(final EncogWriteHelper out) {
		out.addSection("TASKS");
		final List<String> list = new ArrayList<String>();
		list.addAll(this.script.getTasks().keySet());
		Collections.sort(list);
		for (final String key : list) {
			final AnalystTask task = this.script.getTask(key);
			out.addSubSection(task.getName());
			for (final String line : task.getLines()) {
				out.addLine(line);
			}
		}
	}
}
