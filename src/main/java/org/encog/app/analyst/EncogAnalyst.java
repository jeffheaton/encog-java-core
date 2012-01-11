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
package org.encog.app.analyst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.encog.app.analyst.analyze.PerformAnalysis;
import org.encog.app.analyst.commands.Cmd;
import org.encog.app.analyst.commands.CmdBalance;
import org.encog.app.analyst.commands.CmdCluster;
import org.encog.app.analyst.commands.CmdCreate;
import org.encog.app.analyst.commands.CmdEvaluate;
import org.encog.app.analyst.commands.CmdEvaluateRaw;
import org.encog.app.analyst.commands.CmdGenerate;
import org.encog.app.analyst.commands.CmdNormalize;
import org.encog.app.analyst.commands.CmdRandomize;
import org.encog.app.analyst.commands.CmdReset;
import org.encog.app.analyst.commands.CmdSegregate;
import org.encog.app.analyst.commands.CmdSet;
import org.encog.app.analyst.commands.CmdTrain;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.app.quant.QuantTask;
import org.encog.bot.BotUtil;
import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.train.MLTrain;
import org.encog.util.Format;
import org.encog.util.logging.EncogLogging;

/**
 * The Encog Analyst runs Encog Analyst Script files (EGA) to perform many
 * common machine learning tasks. It is very much like Maven or ANT for Encog.
 * Encog analyst files are made up of configuration information and tasks. Tasks
 * are series of commands that make use of the configuration information to
 * process CSV files.
 * 
 * 
 */
public class EncogAnalyst {

	/**
	 * The name of the task that SHOULD everything.
	 */
	public static final String TASK_FULL = "task-full";

	/**
	 * The analyst script.
	 */
	private final AnalystScript script = new AnalystScript();
	
	/**
	 * The listeners.
	 */
	private final List<AnalystListener> listeners 
		= new ArrayList<AnalystListener>();
	
	/**
	 * The update time for a download.
	 */
	public static final int UPDATE_TIME = 10;
	
	/**
	 * The current task.
	 */
	private QuantTask currentQuantTask = null;
	
	/**
	 * The commands.
	 */
	private final Map<String, Cmd> commands = new HashMap<String, Cmd>();
	
	/**
	 * The max iterations, -1 unlimited.
	 */
	private int maxIteration = -1;
	
	/**
	 * Holds a copy of the original property data, used to revert.
	 */
	private Map<String, String> revertData;
	
	/**
	 * The method currently being trained, or null if that method should not 
	 * be modified, or we are not training.
	 */
	private MLMethod method;

	/**
	 * Construct the Encog analyst.
	 */
	public EncogAnalyst() {
		addCommand(new CmdCreate(this));
		addCommand(new CmdEvaluate(this));
		addCommand(new CmdEvaluateRaw(this));
		addCommand(new CmdGenerate(this));
		addCommand(new CmdNormalize(this));
		addCommand(new CmdRandomize(this));
		addCommand(new CmdSegregate(this));
		addCommand(new CmdTrain(this));
		addCommand(new CmdBalance(this));
		addCommand(new CmdSet(this));
		addCommand(new CmdReset(this));
		addCommand(new CmdCluster(this));
	}

	/**
	 * Add a listener.
	 * @param listener The listener to add.
	 */
	public final void addAnalystListener(final AnalystListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Add a command.
	 * @param cmd The command to add.
	 */
	public final void addCommand(final Cmd cmd) {
		this.commands.put(cmd.getName(), cmd);
	}

	/**
	 * Analyze the specified file. Used by the wizard.
	 * @param file The file to analyze.
	 * @param headers True if headers are present.
	 * @param format The format of the file.
	 */
	public final void analyze(final File file, final boolean headers,
			final AnalystFileFormat format) {
		this.script.getProperties().setFilename(AnalystWizard.FILE_RAW,
				file.toString());

		this.script.getProperties().setProperty(
				ScriptProperties.SETUP_CONFIG_INPUT_HEADERS, headers);

		final PerformAnalysis a = new PerformAnalysis(this.script,
				file.toString(), headers, format);
		a.process(this);

	}

	/**
	 * Determine the input count.  This is the actual number of columns.
	 * @return The input count.
	 */
	public final int determineInputCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isInput() && !field.isIgnored()) {
				result += field.getColumnsNeeded();
			}
		}
		return result;
	}

	/**
	 * Determine the input field count, the fields are higher-level 
	 * than columns.
	 * @return The input field count.
	 */
	public final int determineInputFieldCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isInput() && !field.isIgnored()) {
				result++;
			}

		}
		return result;
	}

	/**
	 * Determine the output count, this is the number of output 
	 * columns needed.
	 * @return The output count.
	 */
	public final int determineOutputCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isOutput() && !field.isIgnored()) {
				result += field.getColumnsNeeded();
			}
		}
		return result;
	}

	/**
	 * Determine the number of output fields.  Fields are higher 
	 * level than columns.
	 * @return The output field count.
	 */
	public final int determineOutputFieldCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isOutput() && !field.isIgnored()) {
				result++;
			}

		}
		
		if( this.method instanceof BayesianNetwork ) {
			result++;
		}
		
		return result;
	}

	/**
	 * Determine how many unique columns there are.  Timeslices are not 
	 * counted multiple times.
	 * @return The number of columns.
	 */
	public final int determineUniqueColumns() {
		final Map<String, Object> used = new HashMap<String, Object>();
		int result = 0;

		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (!field.isIgnored()) {
				final String name = field.getName();
				if (!used.containsKey(name)) {
					result += field.getColumnsNeeded();
					used.put(name, null);
				}
			}
		}
		return result;
	}

	/**
	 * Determine the unique input field count.  Timeslices are not 
	 * counted multiple times.
	 * @return The number of unique input fields.
	 */
	public final int determineUniqueInputFieldCount() {
		final Map<String, Object> map = new HashMap<String, Object>();

		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (!map.containsKey(field.getName())) {
				if (field.isInput() && !field.isIgnored()) {
					result++;
					map.put(field.getName(), null);
				}
			}
		}
		return result;
	}
	
	/**
	 * Determine the total input field count, minus ignored fields.
	 * @return The number of unique input fields.
	 */
	public final int determineTotalInputFieldCount() {

		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isInput() && !field.isIgnored()) {
				result+=field.getColumnsNeeded();
			}
		}

		return result;
	}

	/**
	 * Determine the unique output field count.  Do not count timeslices 
	 * multiple times.
	 * @return The unique output field count.
	 */
	public final int determineUniqueOutputFieldCount() {
		final Map<String, Object> map = new HashMap<String, Object>();
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (!map.containsKey(field.getName())) {
				if (field.isOutput() && !field.isIgnored()) {
					result++;
				}
				map.put(field.getName(), null);
			}
		}
		return result;
	}

	/**
	 * Download a raw file from the Internet.
	 */
	public final void download() {
		final URL sourceURL = this.script.getProperties().getPropertyURL(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_FILE);

		final String rawFile = this.script.getProperties().getPropertyFile(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE);
		
		final File rawFilename = getScript().resolveFilename(rawFile);

		if (!rawFilename.exists()) {
			downloadPage(sourceURL, rawFilename);
		}
	}

	/**
	 * Down load a file from the specified URL, uncompress if needed.
	 * @param url THe URL.
	 * @param file The file to down load into.
	 */
	private void downloadPage(final URL url, final File file) {
		
		FileOutputStream fos = null;
		InputStream is = null;
		FileInputStream fis = null;
		GZIPInputStream gis = null;

		
		try {
			// download the URL
			long size = 0;
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];

			final File tempFile = new File(file.getParentFile(), "temp.tmp");

			int length;
			int lastUpdate = 0;

			fos = new FileOutputStream(tempFile);
			is = url.openStream();

			do {
				length = is.read(buffer);

				if (length >= 0) {
					fos.write(buffer, 0, length);
					size += length;
				}

				if (lastUpdate > UPDATE_TIME) {
					report(0, (int) (size / Format.MEMORY_MEG),
							"Downloading... " + Format.formatMemory(size));
					lastUpdate = 0;
				}
				lastUpdate++;
			} while (length >= 0);

			fos.close();
			fos = null;
			
			// unzip if needed

			if (url.toString().toLowerCase().endsWith(".gz")) {
				fis = new FileInputStream(tempFile);
				gis = new GZIPInputStream(fis);
				fos = new FileOutputStream(file);

				size = 0;
				lastUpdate = 0;

				do {
					length = gis.read(buffer);

					if (length >= 0) {
						fos.write(buffer, 0, length);
						size += length;
					}

					if (lastUpdate > UPDATE_TIME) {
						report(0, (int) (size / Format.MEMORY_MEG),
"Uncompressing... " + Format.formatMemory(size));
						lastUpdate = 0;
					}
					lastUpdate++;
				} while (length >= 0);

				tempFile.delete();

			} else {
				// rename the temp file to the actual file
				file.delete();
				tempFile.renameTo(file);
			}

		} catch (final IOException e) {
			throw new AnalystError(e);
		} finally {
			if( fos!=null ) {
				try {
					fos.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
			if( is!=null ) {
				try {
					is.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
			if( fis!=null ) {
				try {
					fis.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
			if( gis!=null ) {
				try {
					gis.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
 		}
	}

	/**
	 * Execute a task.
	 * @param task The task to execute.
	 */
	public final void executeTask(final AnalystTask task) {
		final int total = task.getLines().size();
		int current = 1;
		for (String line : task.getLines()) {
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, 
					"Execute analyst line: " + line);
			reportCommandBegin(total, current, line);
			line = line.trim();
			boolean canceled = false;
			String command;
			String args;

			final String line2 = line.trim();
			final int index = line2.indexOf(' ');
			if (index != -1) {
				command = line2.substring(0, index).toUpperCase();
				args = line2.substring(index + 1);
			} else {
				command = line2.toUpperCase();
				args = "";
			}

			final Cmd cmd = this.commands.get(command);

			if (cmd != null) {
				canceled = cmd.executeCommand(args);
			} else {
				throw new AnalystError("Unknown Command: " + line);
			}

			reportCommandEnd(canceled);
			setCurrentQuantTask(null);
			current++;

			if (shouldStopAll()) {
				break;
			}
		}
	}

	/**
	 * Execute a task.
	 * @param name The name of the task to execute.
	 */
	public final void executeTask(final String name) {
		EncogLogging.log(EncogLogging.LEVEL_INFO, 
				"Analyst execute task:" + name);
		final AnalystTask task = this.script.getTask(name);
		if (task == null) {
			throw new AnalystError("Can't find task: " + name);
		}

		executeTask(task);
	}

	/**
	 * @return The lag depth.
	 */
	public final int getLagDepth() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.getTimeSlice() < 0) {
				result = Math.max(result, Math.abs(field.getTimeSlice()));
			}
		}
		return result;
	}

	/**
	 * @return The lead depth.
	 */
	public final int getLeadDepth() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.getTimeSlice() > 0) {
				result = Math.max(result, field.getTimeSlice());
			}
		}
		return result;
	}

	/**
	 * @return the listeners
	 */
	public final List<AnalystListener> getListeners() {
		return this.listeners;
	}

	/**
	 * @return The max iterations.
	 */
	public final int getMaxIteration() {
		return this.maxIteration;
	}

	/**
	 * @return The reverted data.
	 */
	public final Map<String, String> getRevertData() {
		return this.revertData;
	}

	/**
	 * @return the script
	 */
	public final AnalystScript getScript() {
		return this.script;
	}

	/**
	 * Load the specified script file.
	 * @param file The file to load.
	 */
	public final void load(final File file) {
		InputStream fis = null;
		this.script.setBasePath(file.getParent());

		try {
			fis = new FileInputStream(file);
			load(fis);
		} catch (final IOException ex) {
			throw new AnalystError(ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					throw new AnalystError(e);
				}
			}
		}
	}

	/**
	 * Load from an input stream.
	 * @param stream The stream to load from.
	 */
	public final void load(final InputStream stream) {
		this.script.load(stream);
		this.revertData = this.script.getProperties().prepareRevert();
	}

	/**
	 * Load from the specified filename.
	 * @param filename The filename to load from.
	 */
	public final void load(final String filename) {
		load(new File(filename));
	}

	/**
	 * Remove a listener.
	 * @param listener The listener to remove.
	 */
	public final void removeAnalystListener(final AnalystListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Report progress.
	 * @param total The total units.
	 * @param current The current unit.
	 * @param message The message.
	 */
	private void report(final int total, final int current, 
			final String message) {
		for (final AnalystListener listener : this.listeners) {
			listener.report(total, current, message);
		}
	}

	/**
	 * Report a command has begin.
	 * @param total The total units.
	 * @param current The current unit.
	 * @param name The command name.
	 */
	private void reportCommandBegin(final int total, final int current,
			final String name) {
		for (final AnalystListener listener : this.listeners) {
			listener.reportCommandBegin(total, current, name);
		}
	}

	/**
	 * Report a command has ended.
	 * @param canceled Was the command canceled.
	 */
	private void reportCommandEnd(final boolean canceled) {
		for (final AnalystListener listener : this.listeners) {
			listener.reportCommandEnd(canceled);
		}
	}

	/**
	 * Report training.
	 * @param train The trainer.
	 */
	public final void reportTraining(final MLTrain train) {
		for (final AnalystListener listener : this.listeners) {
			listener.reportTraining(train);
		}
	}

	/**
	 * Report that training has begun.
	 */
	public final void reportTrainingBegin() {
		for (final AnalystListener listener : this.listeners) {
			listener.reportTrainingBegin();
		}
	}

	/**
	 * Report that training has ended.
	 */
	public final void reportTrainingEnd() {
		for (final AnalystListener listener : this.listeners) {
			listener.reportTrainingEnd();
		}
	}

	/**
	 * Save the script to a file.
	 * @param file The file to save to.
	 */
	public final void save(final File file) {
		OutputStream fos = null;

		try {
			this.script.setBasePath(file.getParent());
			fos = new FileOutputStream(file);
			save(fos);
		} catch (final IOException ex) {
			throw new AnalystError(ex);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (final IOException e) {
					throw new AnalystError(e);
				}
			}
		}
	}

	/**
	 * Save the script to a stream.
	 * @param stream The stream to save to.
	 */
	public final void save(final OutputStream stream) {
		this.script.save(stream);

	}

	/**
	 * Save the script to a filename.
	 * @param filename The filename to save to.
	 */
	public final void save(final String filename) {
		save(new File(filename));
	}

	/**
	 * Set the current task.
	 * @param task The current task.
	 */
	public final synchronized void setCurrentQuantTask(final QuantTask task) {
		this.currentQuantTask = task;
	}

	/**
	 * Set the max iterations.
	 * @param i The value for max iterations.
	 */
	public final void setMaxIteration(final int i) {
		this.maxIteration = i;
	}

	/**
	 * Should all commands be stopped.
	 * @return True, if all commands should be stopped.
	 */
	private boolean shouldStopAll() {
		for (final AnalystListener listener : this.listeners) {
			if (listener.shouldShutDown()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Should the current command be stopped.
	 * @return True if the current command should be stopped.
	 */
	public final boolean shouldStopCommand() {
		for (final AnalystListener listener : this.listeners) {
			if (listener.shouldStopCommand()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Stop the current task.
	 */
	public final synchronized void stopCurrentTask() {
		if (this.currentQuantTask != null) {
			this.currentQuantTask.requestStop();
		}
	}

	/**
	 * @return True, if any field has a time slice.
	 */
	public final boolean isTimeSeries() {
		for (AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.getTimeSlice() != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the method
	 */
	public MLMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(MLMethod method) {
		this.method = method;
	}

	public int determineTotalColumns() {
		int result = 0;

		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (!field.isIgnored()) {
				result += field.getColumnsNeeded();
			}
		}
		return result;
	}
	
	

}
