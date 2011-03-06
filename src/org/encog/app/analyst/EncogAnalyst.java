package org.encog.app.analyst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.encog.app.analyst.analyze.PerformAnalysis;
import org.encog.app.analyst.commands.Cmd;
import org.encog.app.analyst.commands.CmdCreate;
import org.encog.app.analyst.commands.CmdEvaluate;
import org.encog.app.analyst.commands.CmdGenerate;
import org.encog.app.analyst.commands.CmdNormalize;
import org.encog.app.analyst.commands.CmdRandomize;
import org.encog.app.analyst.commands.CmdSegregate;
import org.encog.app.analyst.commands.CmdTrain;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.app.quant.QuantTask;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.bot.BotUtil;
import org.encog.engine.util.Format;
import org.encog.ml.MLTrain;
import org.encog.neural.networks.training.Train;
import org.encog.util.csv.CSVFormat;

public class EncogAnalyst {

	public final static String ACTION_ANALYZE = "ANALYZE";

	public static final String TASK_FULL = "task-full";

	private AnalystScript script = new AnalystScript();
	private List<AnalystListener> listeners = new ArrayList<AnalystListener>();
	private QuantTask currentQuantTask = null;
	private Map<String,Cmd> commands = new HashMap<String,Cmd>();
	private final CmdEvaluate evaluation;
	
	public EncogAnalyst() {
		addCommand(new CmdCreate(this));
		addCommand(this.evaluation = new CmdEvaluate(this));
		addCommand(new CmdGenerate(this));
		addCommand(new CmdNormalize(this));
		addCommand(new CmdRandomize(this));
		addCommand(new CmdSegregate(this));
		addCommand(new CmdTrain(this));		
	}

	public void analyze(File file, boolean headers, AnalystFileFormat format) {
		script.getProperties().setFilename(AnalystWizard.FILE_RAW, file.toString());
				
		script.getProperties().setProperty(ScriptProperties.SETUP_CONFIG_inputHeaders, headers);
		
		PerformAnalysis a = new PerformAnalysis(script, file.toString(),
				headers, format);
		a.process(this);

	}
	
	public void addCommand(Cmd cmd) {
		this.commands.put(cmd.getName(), cmd);
	}

	public void clear() {

	}

	public void load(String filename) {
		load(new File(filename));
	}

	public void save(String filename) {
		save(new File(filename));
	}

	public void save(File file) {
		OutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			save(fos);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					throw new AnalystError(e);
				}
		}
	}

	public void load(File file) {
		InputStream fis = null;

		try {
			fis = new FileInputStream(file);
			load(fis);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new AnalystError(e);
				}
		}
	}

	public void save(OutputStream stream) {
		this.script.save(stream);

	}

	public void load(InputStream stream) {
		this.script.load(stream);
	}

	/**
	 * @return the script
	 */
	public AnalystScript getScript() {
		return script;
	}

	private void downloadPage(final URL url, final File file) {
		try {
			// download the URL
			long size = 0;
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];

			File tempFile = new File(file.getParentFile(), "temp.tmp");

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
				FileInputStream fis = new FileInputStream(tempFile);
				GZIPInputStream gis = new GZIPInputStream(fis);
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

	private void reportCommandBegin(int total, int current, String name) {
		for (AnalystListener listener : this.listeners) {
			listener.reportCommandBegin(total, current, name);
		}
	}

	private void reportCommandEnd(boolean canceled) {
		for (AnalystListener listener : this.listeners) {
			listener.reportCommandEnd(canceled);
		}
	}

	public void reportTrainingBegin() {
		for (AnalystListener listener : this.listeners) {
			listener.reportTrainingBegin();
		}
	}

	public void reportTrainingEnd() {
		for (AnalystListener listener : this.listeners) {
			listener.reportTrainingEnd();
		}
	}

	public void reportTraining(MLTrain train) {
		for (AnalystListener listener : this.listeners) {
			listener.reportTraining(train);
		}
	}

	private void report(int total, int current, String message) {
		for (AnalystListener listener : this.listeners) {
			listener.report(total, current, message);
		}
	}

	private boolean shouldStopAll() {
		for (AnalystListener listener : this.listeners) {
			if (listener.shouldShutDown()) {
				return true;
			}
		}
		return false;
	}

	public boolean shouldStopCommand() {
		for (AnalystListener listener : this.listeners) {
			if (listener.shouldStopCommand()) {
				return true;
			}
		}
		return false;
	}

	public void download() {
		URL sourceURL = this.script.getProperties().getPropertyURL(
				ScriptProperties.HEADER_DATASOURCE_sourceFile);

		String rawFile = this.script.getProperties().getPropertyFile(
				ScriptProperties.HEADER_DATASOURCE_rawFile);
		File rawFilename = new File(this.script.getProperties()
				.getFilename(rawFile));

		if (!rawFilename.exists())
			downloadPage(sourceURL, rawFilename);
	}

	public void executeTask(AnalystTask task) {
		int total = task.getLines().size();
		int current = 1;
		for (String line : task.getLines()) {
			this.reportCommandBegin(total, current, line);
			line = line.trim();
			boolean canceled = false;
			
			Cmd cmd = this.commands.get(line.toUpperCase());
			
			if( cmd!=null ) {
				canceled = cmd.executeCommand();
			} else {
				throw new AnalystError("Unknown Command: " + line);
			}
			
			this.reportCommandEnd(canceled);
			setCurrentQuantTask(null);
			current++;

			if (this.shouldStopAll())
				break;
		}
	}

	public void executeTask(String name) {
		AnalystTask task = this.script.getTask(name);
		if (task == null) {
			throw new AnalystError("Can't find task: " + name);
		}

		executeTask(task);
	}


	/**
	 * @return the listeners
	 */
	public List<AnalystListener> getListeners() {
		return listeners;
	}

	public void addAnalystListener(AnalystListener listener) {
		this.listeners.add(listener);
	}

	public void removeAnalystListener(AnalystListener listener) {
		this.listeners.remove(listener);
	}

	public synchronized void setCurrentQuantTask(QuantTask task) {
		this.currentQuantTask = task;
	}

	public synchronized void stopCurrentTask() {
		if (this.currentQuantTask != null) {
			this.currentQuantTask.requestStop();
		}
	}
	
	public int[] determineInputFields() {
		List<Integer> fields = new ArrayList<Integer>();
		String targetField = this.script.getProperties().getPropertyString(ScriptProperties.DATA_CONFIG_targetField);
		
		// calculate size of each field
		int currentIndex = 0;
		for(NormalizedField norm : this.script.getNormalize().getNormalizedFields() ) {
			int cols = norm.getColumnsNeeded();
			
			if( !norm.getName().equalsIgnoreCase(targetField) ) {
				for(int i=0;i<cols;i++) {
					fields.add(currentIndex++);
				}
			} else {
				currentIndex+=cols;
			}
		}
		
		// allocate result array
		int[] result = new int[fields.size()];
		for(int i=0;i<result.length;i++) {
			result[i] = fields.get(i);
		}
		
		return result;
	}
	
	public int[] determineIdealFields() {
		List<Integer> fields = new ArrayList<Integer>();
		String targetField = this.script.getProperties().getPropertyString(ScriptProperties.DATA_CONFIG_targetField);
		
		// calculate size of each field
		int currentIndex = 0;
		for(NormalizedField norm : this.script.getNormalize().getNormalizedFields() ) {
			int cols = norm.getColumnsNeeded();
			
			if( norm.getName().equalsIgnoreCase(targetField) ) {
				for(int i=0;i<cols;i++) {
					fields.add(currentIndex++);
				}
			} else {
				currentIndex+=cols;
			}
		}
		
		// allocate result array
		int[] result = new int[fields.size()];
		for(int i=0;i<result.length;i++) {
			result[i] = fields.get(i);
		}
		
		return result;
	}

}
