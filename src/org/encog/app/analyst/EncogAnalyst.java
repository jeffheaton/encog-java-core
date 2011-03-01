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
import org.encog.app.analyst.evaluate.AnalystEvaluateCSV;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.EncogAnalystConfig;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.app.quant.QuantTask;
import org.encog.app.quant.evaluate.EvaluateCSV;
import org.encog.app.quant.normalize.NormalizationStats;
import org.encog.app.quant.normalize.NormalizeCSV;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.app.quant.segregate.SegregateCSV;
import org.encog.app.quant.segregate.SegregateTargetPercent;
import org.encog.app.quant.shuffle.ShuffleCSV;
import org.encog.bot.BotUtil;
import org.encog.engine.util.Format;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.EncogEGBFile;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class EncogAnalyst {

	public final static String ACTION_ANALYZE = "ANALYZE";

	public static final String TASK_FULL = "task-full";

	private AnalystScript script = new AnalystScript();
	private List<AnalystListener> listeners = new ArrayList<AnalystListener>();
	private Map<String, Integer> classCorrect = new HashMap<String, Integer>();
	private Map<String, Integer> classCount = new HashMap<String, Integer>();
	private QuantTask currentQuantTask = null;

	public void analyze(File file, boolean headers, CSVFormat format) {
		script.getConfig().setFilename(AnalystWizard.FILE_RAW,
				file.toString());
		script.getConfig().setCSVFormat(format);
		script.getConfig().setInputHeaders(headers);
		PerformAnalysis a = new PerformAnalysis(script, file.toString(),
				headers, CSVFormat.ENGLISH);
		a.process(this);

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

	public boolean normalize() {
		//this.report.reportPhase(0, 0, "Normalizing");
		// mark generated
		this.script.markGenerated(this.script.getNormalize().getTargetFile());

		// get filenames
		String sourceFile = this.script.getConfig().getFilename(
				this.script.getNormalize().getSourceFile());
		String targetFile = this.script.getConfig().getFilename(
				this.script.getNormalize().getTargetFile());

		// prepare to normalize
		NormalizeCSV norm = new NormalizeCSV();
		setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(this));
		NormalizedField[] normFields = this.script.getNormalize()
				.getNormalizedFields();
		NormalizationStats stats = new NormalizationStats(normFields);

		boolean headers = this.script.expectInputHeaders(this.script
				.getNormalize().getSourceFile());
		norm.analyze(sourceFile, headers, this.script.getConfig()
				.getCSVFormat(), stats);
		norm.setProduceOutputHeaders(this.script.getConfig().isOutputHeaders());
		norm.normalize(targetFile);
		setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	public boolean randomize() {
		//this.report.reportPhase(0, 0, "Randomizing");

		// mark generated
		this.script.markGenerated(this.script.getRandomize().getTargetFile());

		// get filenames
		String sourceFile = this.script.getConfig().getFilename(
				this.script.getRandomize().getSourceFile());
		String targetFile = this.script.getConfig().getFilename(
				this.script.getRandomize().getTargetFile());

		// prepare to normalize
		ShuffleCSV norm = new ShuffleCSV();
		setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(this));
		boolean headers = this.script.expectInputHeaders(this.script
				.getRandomize().getSourceFile());
		norm.analyze(sourceFile, headers, this.script.getConfig()
				.getCSVFormat());
		norm.process(targetFile);
		setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	public boolean segregate() {

		// get filenames		
		String inputFile = this.script.getConfig().getFilename(
				this.script.getSegregate().getSourceFile());

		// prepare to segregate
		boolean headers = this.script.expectInputHeaders(this.script
				.getSegregate().getSourceFile());
		SegregateCSV seg = new SegregateCSV();
		setCurrentQuantTask(seg);
		for (AnalystSegregateTarget target : this.script.getSegregate()
				.getSegregateTargets()) {
			String filename = this.script.getConfig().getFilename(
					target.getFile());
			seg.getTargets().add(
					new SegregateTargetPercent(filename, target.getPercent()));
			// mark generated
			this.script.markGenerated(target.getFile());
		}
		seg.setReport(new AnalystReportBridge(this));
		seg.analyze(inputFile, headers, this.script.getConfig().getCSVFormat());

		seg.process();
		setCurrentQuantTask(null);
		return seg.shouldStop();
	}

	public boolean generate() {

		// mark generated
		this.script.markGenerated(this.script.getNormalize().getTargetFile());

		// get filenames
		String sourceFile = this.script.getConfig().getFilename(
				this.script.getGenerate().getSourceFile());
		String targetFile = this.script.getConfig().getFilename(
				this.script.getGenerate().getTargetFile());
		int input = this.script.getGenerate().getInput();
		int ideal = this.script.getGenerate().getIdeal();

		boolean headers = this.script.expectInputHeaders(this.script
				.getGenerate().getSourceFile());
		EncogUtility.convertCSV2Binary(sourceFile, targetFile, input, ideal,
				headers);
		return false;
	}

	public boolean create() {

		// get filenames
		String trainingFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getTrainingFile());
		String resourceFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getResourceFile());
		String resource = this.script.getMachineLearning().getResourceName();
		String type = this.script.getMachineLearning().getMLType();
		String arch = this.script.getMachineLearning().getMLArchitecture();

		EncogEGBFile egb = new EncogEGBFile(new File(trainingFile));
		egb.open();
		int input = egb.getInputCount();
		int ideal = egb.getIdealCount();
		egb.close();

		EncogMemoryCollection encog = new EncogMemoryCollection();
		if (new File(resourceFile).exists()) {
			encog.load(resourceFile);
		}

		MLMethodFactory factory = new MLMethodFactory();
		MLMethod obj = factory.create(type, arch, input, ideal);

		encog.add(resource, (EncogPersistedObject) obj);
		encog.save(resourceFile);
		return false;
	}

	public boolean train() {

		// get filenames
		String trainingFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getTrainingFile());
		String resourceFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getResourceFile());
		String resource = this.script.getMachineLearning().getResourceName();

		NeuralDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.load(resourceFile);

		EncogPersistedObject method = encog.find(resource);

		Train train = null;
		boolean singleIteration = false;

		if (method instanceof BasicNetwork) {
			train = new ResilientPropagation((BasicNetwork) method, trainingSet);
			singleIteration = false;
		} else if (method instanceof SVM) {
			train = new SVMTrain((SVM) method, trainingSet);
			singleIteration = true;
		}

		reportTrainingBegin();

		if (!singleIteration) {
			do {
				train.iteration();
				this.reportTraining(train);
			} while (train.getError() > 0.01 && !this.shouldStopCommand());
		} else {
			if( method instanceof SVM ) {
				((SVMTrain)train).train();
				double error = EncogUtility.calculateRegressionError((SVM)method, trainingSet);
				train.setError(error);
				train.setIteration(1);
				this.reportTraining(train);
			} else {
			train.iteration();
			this.reportTraining(train);
			}
		}

		reportTrainingEnd();

		encog.save(resourceFile);
		return this.shouldStopCommand();
	}

	public void evaluateRaw() {

		// get filenames
		String evalFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getEvalFile());
		String resourceFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getResourceFile());
		String resource = this.script.getMachineLearning().getResourceName();

		String outputFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getOutputFile());

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.load(resourceFile);
		MLRegression method = (MLRegression) encog.find(resource);

		boolean headers = this.script.expectInputHeaders(this.script
				.getNormalize().getSourceFile());

		EvaluateCSV eval = new EvaluateCSV();
		setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(this));
		eval.analyze(evalFile, headers, this.script.getConfig().getCSVFormat());
		eval.process(outputFile, method);
		setCurrentQuantTask(null);
	}

	public boolean evaluate() {

		// get filenames
		String evalFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getEvalFile());
		String resourceFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getResourceFile());
		String resource = this.script.getMachineLearning().getResourceName();

		String outputFile = this.script.getConfig().getFilename(
				this.script.getMachineLearning().getOutputFile());

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.load(resourceFile);
		MLRegression method = (MLRegression) encog.find(resource);

		boolean headers = this.script.expectInputHeaders(this.script
				.getNormalize().getSourceFile());

		AnalystEvaluateCSV eval = new AnalystEvaluateCSV();
		setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(this));
		eval.analyze(evalFile, headers, this.script.getConfig().getCSVFormat());
		eval.process(outputFile, this, method);
		setCurrentQuantTask(null);
		this.classCorrect = eval.getClassCorrect();
		this.classCount = eval.getClassCount();
		return eval.shouldStop();

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

	private void reportTrainingBegin() {
		for (AnalystListener listener : this.listeners) {
			listener.reportTrainingBegin();
		}
	}

	private void reportTrainingEnd() {
		for (AnalystListener listener : this.listeners) {
			listener.reportTrainingEnd();
		}
	}

	private void reportTraining(Train train) {
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

	private boolean shouldStopCommand() {
		for (AnalystListener listener : this.listeners) {
			if (listener.shouldStopCommand()) {
				return true;
			}
		}
		return false;
	}

	public void download() {
		try {
			String sourceURL = this.script.getInformation().getDataSource();
			String rawFile = this.script.getInformation().getRawFile();
			File rawFilename = new File(this.script.getConfig().getFilename(
					rawFile));
			URL url = new URL(sourceURL);
			if (!rawFilename.exists())
				downloadPage(url, rawFilename);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		}
	}

	public Map<String, Integer> getClassCorrect() {
		return classCorrect;
	}

	public void setClassCorrect(Map<String, Integer> classCorrect) {
		this.classCorrect = classCorrect;
	}

	public Map<String, Integer> getClassCount() {
		return classCount;
	}

	public void setClassCount(Map<String, Integer> classCount) {
		this.classCount = classCount;
	}

	public void executeTask(AnalystTask task) {
		int total = task.getLines().size();
		int current = 1;
		for (String line : task.getLines()) {
			this.reportCommandBegin(total, current, line);
			line = line.trim();
			boolean canceled = false;
			if (line.equals("randomize")) {
				canceled = randomize();
			} else if (line.equals("segregate")) {
				canceled = segregate();
			} else if (line.equals("normalize")) {
				canceled = normalize();
			} else if (line.equals("generate")) {
				canceled = generate();
			} else if (line.equals("create")) {
				canceled = create();
			} else if (line.equals("train")) {
				canceled = train();
			} else if (line.equals("evaluate")) {
				canceled = evaluate();
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

	public String evalToString() {
		List<String> list = new ArrayList<String>();
		list.addAll(this.classCount.keySet());
		Collections.sort(list);

		StringBuilder result = new StringBuilder();
		for (String key : list) {
			result.append(key);
			result.append(" ");
			double correct = classCorrect.get(key);
			double count = classCount.get(key);

			result.append(Format.formatInteger((int) correct));
			result.append('/');
			result.append(Format.formatInteger((int) count));
			result.append('(');
			result.append(Format.formatPercent(correct / count));
			result.append(")\n");
		}

		return result.toString();
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

	private synchronized void setCurrentQuantTask(QuantTask task) {
		this.currentQuantTask = task;
	}

	public synchronized void stopCurrentTask() {
		if (this.currentQuantTask != null) {
			this.currentQuantTask.requestStop();
		}
	}

}
