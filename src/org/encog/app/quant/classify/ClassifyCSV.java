package org.encog.app.quant.classify;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.csv.ReadCSV;

/**
 * Used to classify a CSV file. Often a CSV file will contain a field that
 * specifies a class that a row belongs two. This "class code"/"class number"
 * must be formatted to be processed by a neural network or other machine
 * learning construct.
 * 
 */
public class ClassifyCSV extends BasicFile {

	/**
	 * Holds stats on the field that is to be classified.
	 */
	private ClassifyStats classify;

	/**
	 * @return The stats on a field that is to be classified.
	 */
	public ClassifyStats getStats() {
		return this.classify;
	}

	/**
	 * Construct the object and set the defaults.
	 */
	public ClassifyCSV() {
		this.classify = new ClassifyStats();
		this.classify.setHigh(1);
		this.classify.setLow(-1);
	}

	/**
	 * Analyze the file.
	 * @param inputFile The input file to analyze.
	 * @param headers True, if the input file has headers.
	 * @param format The format of the input file.
	 * @param classField The field to be classified.
	 */
	public void analyze(String inputFile, boolean headers, CSVFormat format,
			int classField) {
		List<String> classesFound = new ArrayList<String>();
		this.setInputFilename(inputFile);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);
		this.classify.setClassField(classField);

		this.setAnalyzed(true);

		resetStatus();
		int recordCount = 0;
		ReadCSV csv = new ReadCSV(this.getInputFilename(),
				this.isExpectInputHeaders(), this.getInputFormat());
		while (csv.next()) {
			updateStatus(true);
			String key = csv.get(classField);
			if (!classesFound.contains(key))
				classesFound.add(key);
			recordCount++;
		}
		this.setRecordCount(recordCount);
		this.setColumnCount(csv.getColumnCount());

		readHeaders(csv);
		csv.close();

		// determine if class is numeric
		this.classify.setNumeric(true);
		for (String key : classesFound) {
			this.classify.setNumeric(false);
			try {
				Integer.parseInt(key);
				this.classify.setNumeric(true);
				break;
			} catch (NumberFormatException ex) {

			}
		}

		// sort either by string or numeric
		this.classify.getClasses().clear();
		if (this.classify.isNumeric()) {
			// sort numeric
			int[] temp = new int[classesFound.size()];
			for (int i = 0; i < classesFound.size(); i++)
				temp[i] = Integer.parseInt(classesFound.get(i));
			Arrays.sort(temp);

			// create classes
			for (int i = 0; i < temp.length; i++) {
				this.classify.getClasses().add(new ClassItem("" + temp[i], i));
			}
		} else {
			// sort string
			String[] temp = new String[classesFound.size()];
			for (int i = 0; i < classesFound.size(); i++)
				temp[i] = classesFound.get(i);
			Arrays.sort(temp);

			// create classes
			for (int i = 0; i < temp.length; i++) {
				this.classify.getClasses().add(new ClassItem(temp[i], i));
			}
		}

		this.classify.init();
		reportDone(true);
	}

	/**
	 * Perform the encoding for "one of".
	 * @param classNumber The class number.
	 * @return The encoded columns.
	 */
	private String encodeOneOf(int classNumber) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < this.classify.getClasses().size(); i++) {
			if (i > 0) {
				result.append(this.getInputFormat().getSeparator());
			}

			if (i == classNumber) {
				result.append(this.classify.getHigh());
			} else {
				result.append(this.classify.getLow());
			}
		}
		return result.toString();
	}

	/**
	 * Perform an equilateral encode.
	 * @param classNumber The class number.
	 * @return The class to encode.
	 */
	private String encodeEquilateral(int classNumber) {
		StringBuilder result = new StringBuilder();
		double[] d = this.classify.getEquilateralEncode().encode(classNumber);
		NumberList
				.toList(this.getInputFormat(), this.getPrecision(), result, d);
		return result.toString();
	}
	
	/**
	 * Encode a single field.
	 * @param classNumber The class number to encode.
	 * @return The encoded columns.
	 */
	private String encodeSingleField(int classNumber) {
		StringBuilder result = new StringBuilder();
		result.append(classNumber);
		return result.toString();
	}

	/**
	 * Encode the class.
	 * @param method The encoding method.
	 * @param classNumber The class number.
	 * @return The encoded class.
	 */
	private String encode(ClassifyMethod method, int classNumber) {
		switch (method) {
		case OneOf:
			return encodeOneOf(classNumber);
		case Equilateral:
			return encodeEquilateral(classNumber);
		case SingleField:
			return encodeSingleField(classNumber);
		default:
			return null;
		}
	}


	/**
	 * Prepare the output file, write headers if needed.
	 * @param outputFile The name of the output file.
	 * @param originalName The name of original field.
	 * @param idx The index to insert the orig name.
	 * @return The output stream for the text file.
	 */
	public PrintWriter prepareOutputFile(String outputFile,
			String originalName, int idx) {
		
		try {
			PrintWriter tw = new PrintWriter(new FileWriter(outputFile));
			// write headers, if needed
			if (this.isExpectInputHeaders()) {
				int index = 0;
				StringBuilder line = new StringBuilder();
				for (String str : this.getInputHeadings()) {
					if (index == idx) {
						if (line.length() > 0) {
							line.append(",");
						}

						line.append("\"");
						line.append(originalName);
						line.append("\"");
					}

					if (line.length() > 0) {
						line.append(",");
					}
					line.append("\"");
					line.append(this.getInputHeadings()[index++]);
					line.append("\"");
				}
				tw.println(line.toString());
			}

			return tw;
		} catch (IOException e) {
			throw new QuantError(e);
		}

	}


	/**
	 * Process the file.
	 * @param outputFile The output file.
	 * @param method The classification method.
	 * @param insertAt The column to insert the classified columns at,
	 * or -1 for the end.
	 * @param originalName If not null, include original column and
	 * name it this. Usually null.
	 */
	public void process(String outputFile, ClassifyMethod method, int insertAt,
			String originalName) {
		PrintWriter tw;

		validateAnalyzed();

		this.getStats().setMethod( method );

		if (originalName == null)
			tw = this.prepareOutputFile(outputFile);
		else
			tw = this.prepareOutputFile(outputFile, originalName,
					this.classify.getClassField());

		ReadCSV csv = new ReadCSV(this.getInputFilename(), this.isExpectInputHeaders(),
				this.getInputFormat());
		this.classify.init();

		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			StringBuilder line = new StringBuilder();
			int classNumber = this.classify.lookup(csv
					.get(this.classify.getClassField()));
			boolean inserted = false;

			for (int i = 0; i < this.getColumnCount(); i++) {
				if (i > 0) {
					line.append(this.getInputFormat().getSeparator());
				}

				if (insertAt == i) {
					line.append(encode(method, classNumber));
					line.append(this.getInputFormat().getSeparator());
					inserted = true;
				}

				if (originalName == null && i == this.classify.getClassField()) {
					continue;
				}

				line.append(csv.get(i));
			}

			// if we failed to insert the class field anywhere, then insert it
			// at the end
			if (!inserted) {
				if (line.charAt(line.length() - 1) != this.getInputFormat().getSeparator())
					line.append(this.getInputFormat().getSeparator());
				line.append(encode(method, classNumber));
			}

			tw.println(line.toString());
		}

		csv.close();
		tw.close();
		reportDone(false);
		this.classify.init();
	}

}
