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

import org.encog.NullStatusReportable;
import org.encog.app.analyst.analyze.PerformAnalysis;
import org.encog.app.analyst.evaluate.AnalystEvaluateCSV;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.EncogAnalystConfig;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.quant.evaluate.EvaluateCSV;
import org.encog.app.quant.normalize.NormalizationStats;
import org.encog.app.quant.normalize.NormalizeCSV;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.app.quant.segregate.SegregateCSV;
import org.encog.app.quant.segregate.SegregateTargetPercent;
import org.encog.app.quant.shuffle.ShuffleCSV;
import org.encog.bot.BotUtil;
import org.encog.engine.StatusReportable;
import org.encog.engine.util.Format;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.EncogEGBFile;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class EncogAnalyst {

	public final static String ACTION_ANALYZE = "ANALYZE";

	private AnalystScript script = new AnalystScript();
	private StatusReportable report = new NullStatusReportable();
	private Map<String,Integer> classCorrect = new HashMap<String,Integer>();
	private Map<String,Integer> classCount = new HashMap<String,Integer>();


	public void analyze(File file, boolean headers, CSVFormat format) {
		script.getConfig().setFilename(EncogAnalystConfig.FILE_RAW,
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

	public void normalize() {
		this.report.reportPhase(0, 0, "Normalizing");
		// mark generated
		this.script.markGenerated(this.script.getNormalize().getTargetFile());

		// get filenames
		String sourceFile = this.script.getConfig().getFilename(
				this.script.getNormalize().getSourceFile());
		String targetFile = this.script.getConfig().getFilename(
				this.script.getNormalize().getTargetFile());

		// prepare to normalize
		NormalizeCSV norm = new NormalizeCSV();
		norm.setReport(this.report);
		NormalizedField[] normFields = this.script.getNormalize()
				.getNormalizedFields();
		NormalizationStats stats = new NormalizationStats(normFields);

		boolean headers = this.script.expectInputHeaders(this.script
				.getNormalize().getSourceFile());
		norm.analyze(sourceFile, headers, this.script.getConfig()
				.getCSVFormat(), stats);
		norm.setProduceOutputHeaders(this.script.getConfig().isOutputHeaders());
		norm.normalize(targetFile);
	}

	public void randomize() {
		this.report.reportPhase(0, 0, "Randomizing");

		// mark generated
		this.script.markGenerated(this.script.getRandomize().getTargetFile());

		// get filenames
		String sourceFile = this.script.getConfig().getFilename(
				this.script.getRandomize().getSourceFile());
		String targetFile = this.script.getConfig().getFilename(
				this.script.getRandomize().getTargetFile());

		// prepare to normalize
		ShuffleCSV norm = new ShuffleCSV();
		norm.setReport(this.report);
		boolean headers = this.script.expectInputHeaders(this.script
				.getRandomize().getSourceFile());
		norm.analyze(sourceFile, headers, this.script.getConfig()
				.getCSVFormat());
		norm.process(targetFile);
	}

	public void segregate() {
		this.report.reportPhase(0, 0, "Segregating");
		// get filenames		
		String inputFile = this.script.getConfig().getFilename(
				this.script.getSegregate().getSourceFile());

		// prepare to segregate
		boolean headers = this.script.expectInputHeaders(this.script
				.getSegregate().getSourceFile());
		SegregateCSV seg = new SegregateCSV();
		for (AnalystSegregateTarget target : this.script.getSegregate()
				.getSegregateTargets()) {
			String filename = this.script.getConfig().getFilename(
					target.getFile());
			seg.getTargets().add(
					new SegregateTargetPercent(filename, target.getPercent()));
			// mark generated
			this.script.markGenerated(target.getFile());
		}
		seg.setReport(this.report);
		seg.analyze(inputFile, headers, this.script.getConfig().getCSVFormat());

		seg.process();
	}

	public void generate() {
		this.report.reportPhase(0, 0, "Generating Training");
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
	}

	public void create() {
		this.report.reportPhase(0, 0, "Create Machine Learning Method");
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
		
		encog.add(resource, (EncogPersistedObject)obj);
		encog.save(resourceFile);
	}

	public void train() {
		this.report.reportPhase(0, 0, "Training");
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
		EncogUtility.trainToError((MLMethod) method, trainingSet, 0.01);
		encog.save(resourceFile);
	}

	public void evaluateRaw() {
		this.report.reportPhase(0, 0, "Evaluate with Raw Data");
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
		eval.setReport(this.report);
		eval.analyze(evalFile, headers, this.script.getConfig().getCSVFormat());
		eval.process(outputFile, method);

	}

	public void evaluate() {
		this.report.reportPhase(0, 0, "Evaluate");
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
		eval.setReport(this.report);
		eval.analyze(evalFile, headers, this.script.getConfig().getCSVFormat());
		eval.process(outputFile, this, method);
		
		this.classCorrect = eval.getClassCorrect();
		this.classCount = eval.getClassCount();

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
					fos.write(buffer,0,length);
					size+=length;
				}
				
				if( lastUpdate>10 ) {
					this.report.report(0, (int)(size/Format.MEMORY_MEG), "Downloading... " + Format.formatMemory(size));
					lastUpdate = 0;
				}
				lastUpdate++;
			} while (length >= 0);

			fos.close();
			// unzip if needed
			
			if( url.toString().toLowerCase().endsWith(".gz"))
			{
				FileInputStream fis = new FileInputStream(tempFile);
				GZIPInputStream gis = new GZIPInputStream(fis);
				fos = new FileOutputStream(file);
				
				size = 0;
				lastUpdate = 0;
				
				do {
					length = gis.read(buffer);
					
					if (length >= 0) {
						fos.write(buffer,0,length);
						size+=length;
					}
					
					if( lastUpdate>10 ) {
						this.report.report(0, (int)(size/Format.MEMORY_MEG), "Uncompressing... " + Format.formatMemory(size));
						lastUpdate = 0;
					}
					lastUpdate++;
				} while (length >= 0);
				
				fos.close();
				fis.close();
				gis.close();
				tempFile.delete();
				

			}
			else {			
				// rename the temp file to the actual file
				file.delete();
				tempFile.renameTo(file);
			}
			
		} catch (final IOException e) {
			throw new AnalystError(e);
		}
	}

	public void download() {
		try {			
			String sourceURL = this.script.getInformation().getDataSource(); 
			String rawFile = this.script.getInformation().getRawFile();
			File rawFilename = new File( this.script.getConfig().getFilename(rawFile) );
			URL url = new URL(sourceURL);
			if(!rawFilename.exists())
				downloadPage(url, rawFilename);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		}
	}

	/**
	 * @return the report
	 */
	public StatusReportable getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(StatusReportable report) {
		this.report = report;
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
	
	public String evalToString() {
		List<String> list = new ArrayList<String>();
		list.addAll(this.classCount.keySet());
		Collections.sort(list);
		
		StringBuilder result = new StringBuilder();
		for(String key: list) {
			result.append(key);
			result.append(" ");
			double correct = classCorrect.get(key);
			double count = classCount.get(key);
			
			result.append(Format.formatInteger((int)correct));
			result.append('/');
			result.append(Format.formatInteger((int)count));
			result.append('(');
			result.append(Format.formatPercent(correct/count));
			result.append(")\n");
		}
				
		return result.toString();
	}

}
