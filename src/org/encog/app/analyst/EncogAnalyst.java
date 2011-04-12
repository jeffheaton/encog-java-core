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
import org.encog.engine.util.Format;
import org.encog.ml.MLTrain;

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

	public final static String ACTION_ANALYZE = "ANALYZE";

	public static final String TASK_FULL = "task-full";

	private final AnalystScript script = new AnalystScript();
	private final List<AnalystListener> listeners = new ArrayList<AnalystListener>();
	private QuantTask currentQuantTask = null;
	private final Map<String, Cmd> commands = new HashMap<String, Cmd>();
	private int maxIteration = -1;
	private Map<String, String> revertData;

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

	public void addAnalystListener(final AnalystListener listener) {
		this.listeners.add(listener);
	}

	public void addCommand(final Cmd cmd) {
		this.commands.put(cmd.getName(), cmd);
	}

	public void analyze(final File file, final boolean headers,
			final AnalystFileFormat format) {
		this.script.getProperties().setFilename(AnalystWizard.FILE_RAW,
				file.toString());

		this.script.getProperties().setProperty(
				ScriptProperties.SETUP_CONFIG_INPUT_HEADERS, headers);

		final PerformAnalysis a = new PerformAnalysis(this.script,
				file.toString(), headers, format);
		a.process(this);

	}

	public void clear() {

	}

	public int determineInputCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isInput() && !field.isIgnored()) {
				result += field.getColumnsNeeded();
			}
		}
		return result;
	}

	public int determineInputFieldCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isInput() && !field.isIgnored()) {
				result++;
			}

		}
		return result;
	}

	public int determineOutputCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isOutput() && !field.isIgnored()) {
				result += field.getColumnsNeeded();
			}
		}
		return result;
	}

	public int determineOutputFieldCount() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.isOutput() && !field.isIgnored()) {
				result++;
			}

		}
		return result;
	}

	public int determineUniqueColumns() {
		final Map<String, Object> used = new HashMap<String, Object>();
		int result = 0;

		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (!field.isIgnored()) {
				final String name = field.getName();
				if (!used.containsKey(name)) {
					result += field.getColumnsNeeded();
				}
			}
		}
		return result;
	}

	public int determineUniqueInputFieldCount() {
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

	public int determineUniqueOutputFieldCount() {
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

	public void download() {
		final URL sourceURL = this.script.getProperties().getPropertyURL(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_FILE);

		final String rawFile = this.script.getProperties().getPropertyFile(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE);
		final File rawFilename = new File(this.script.getProperties()
				.getFilename(rawFile));

		if (!rawFilename.exists()) {
			downloadPage(sourceURL, rawFilename);
		}
	}

	private void downloadPage(final URL url, final File file) {
		try {
			// download the URL
			long size = 0;
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];

			final File tempFile = new File(file.getParentFile(), "temp.tmp");

			int length;
			int lastUpdate = 0;

			FileOutputStream fos = new FileOutputStream(tempFile);
			final InputStream is = url.openStream();

			do {
				length = is.read(buffer);

				if (length >= 0) {
					fos.write(buffer, 0, length);
					size += length;
				}

				if (lastUpdate > 10) {
					report(0, (int) (size / Format.MEMORY_MEG),
							"Downloading... " + Format.formatMemory(size));
					lastUpdate = 0;
				}
				lastUpdate++;
			} while (length >= 0);

			fos.close();
			// unzip if needed

			if (url.toString().toLowerCase().endsWith(".gz")) {
				final FileInputStream fis = new FileInputStream(tempFile);
				final GZIPInputStream gis = new GZIPInputStream(fis);
				fos = new FileOutputStream(file);

				size = 0;
				lastUpdate = 0;

				do {
					length = gis.read(buffer);

					if (length >= 0) {
						fos.write(buffer, 0, length);
						size += length;
					}

					if (lastUpdate > 10) {
						report(0, (int) (size / Format.MEMORY_MEG),
								"Uncompressing... " + Format.formatMemory(size));
						lastUpdate = 0;
					}
					lastUpdate++;
				} while (length >= 0);

				fos.close();
				fis.close();
				gis.close();
				tempFile.delete();

			} else {
				// rename the temp file to the actual file
				file.delete();
				tempFile.renameTo(file);
			}

		} catch (final IOException e) {
			throw new AnalystError(e);
		}
	}

	public void executeTask(final AnalystTask task) {
		final int total = task.getLines().size();
		int current = 1;
		for (String line : task.getLines()) {
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

	public void executeTask(final String name) {
		final AnalystTask task = this.script.getTask(name);
		if (task == null) {
			throw new AnalystError("Can't find task: " + name);
		}

		executeTask(task);
	}

	public int getLagDepth() {
		int result = 0;
		for (final AnalystField field : this.script.getNormalize()
				.getNormalizedFields()) {
			if (field.getTimeSlice() < 0) {
				result = Math.max(result, Math.abs(field.getTimeSlice()));
			}
		}
		return result;
	}

	public int getLeadDepth() {
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
	public List<AnalystListener> getListeners() {
		return this.listeners;
	}

	public int getMaxIteration() {
		return this.maxIteration;
	}

	public Map<String, String> getRevertData() {
		return this.revertData;
	}

	/**
	 * @return the script
	 */
	public AnalystScript getScript() {
		return this.script;
	}

	public void load(final File file) {
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

	public void load(final InputStream stream) {
		this.script.load(stream);
		this.revertData = this.script.getProperties().prepareRevert();
	}

	public void load(final String filename) {
		load(new File(filename));
	}

	public void removeAnalystListener(final AnalystListener listener) {
		this.listeners.remove(listener);
	}

	private void report(final int total, final int current, final String message) {
		for (final AnalystListener listener : this.listeners) {
			listener.report(total, current, message);
		}
	}

	private void reportCommandBegin(final int total, final int current,
			final String name) {
		for (final AnalystListener listener : this.listeners) {
			listener.reportCommandBegin(total, current, name);
		}
	}

	private void reportCommandEnd(final boolean canceled) {
		for (final AnalystListener listener : this.listeners) {
			listener.reportCommandEnd(canceled);
		}
	}

	public void reportTraining(final MLTrain train) {
		for (final AnalystListener listener : this.listeners) {
			listener.reportTraining(train);
		}
	}

	public void reportTrainingBegin() {
		for (final AnalystListener listener : this.listeners) {
			listener.reportTrainingBegin();
		}
	}

	public void reportTrainingEnd() {
		for (final AnalystListener listener : this.listeners) {
			listener.reportTrainingEnd();
		}
	}

	public void save(final File file) {
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

	public void save(final OutputStream stream) {
		this.script.save(stream);

	}

	public void save(final String filename) {
		save(new File(filename));
	}

	public synchronized void setCurrentQuantTask(final QuantTask task) {
		this.currentQuantTask = task;
	}

	public void setMaxIteration(final int i) {
		this.maxIteration = i;
	}

	private boolean shouldStopAll() {
		for (final AnalystListener listener : this.listeners) {
			if (listener.shouldShutDown()) {
				return true;
			}
		}
		return false;
	}

	public boolean shouldStopCommand() {
		for (final AnalystListener listener : this.listeners) {
			if (listener.shouldStopCommand()) {
				return true;
			}
		}
		return false;
	}

	public synchronized void stopCurrentTask() {
		if (this.currentQuantTask != null) {
			this.currentQuantTask.requestStop();
		}
	}

}
