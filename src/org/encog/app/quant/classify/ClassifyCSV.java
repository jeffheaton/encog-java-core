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
	}

	/**
	 * Analyze the file.
	 * @param inputFile The input file to analyze.
	 * @param headers True, if the input file has headers.
	 * @param format The format of the input file.
	 * @param classField The field to be classified.
	 */
	public void analyze(String inputFile, boolean headers, CSVFormat format) {

		this.setInputFilename(inputFile);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);
		this.classify.setPrecision(this.getPrecision());
		this.classify.setFormat(format);
				
		this.setAnalyzed(true);


	}
	
	public void addTarget(int classField, ClassifyMethod method, int insertAt, String originalName)
	{
		addTarget(classField,method,1,-1,insertAt,originalName);
	}
	
	public void addTarget(int classField, ClassifyMethod method, double high, double low, int insertAt, String originalName)
	{
		List<String> classesFound = new ArrayList<String>();
		ClassifyTarget target = new ClassifyTarget(this.classify);
		target.setTargetIndex(classField);
		target.setMethod(method);
		target.setInsertAt(insertAt);
		target.setOriginalName(originalName);
		target.setHigh(high);
		target.setLow(low);
		
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
		target.setNumeric(true);
		for (String key : classesFound) {
			target.setNumeric(false);
			try {
				Integer.parseInt(key);
				target.setNumeric(true);
				break;
			} catch (NumberFormatException ex) {

			}
		}

		// sort either by string or numeric
		target.getClasses().clear();
		if (target.isNumeric()) {
			// sort numeric
			int[] temp = new int[classesFound.size()];
			for (int i = 0; i < classesFound.size(); i++)
				temp[i] = Integer.parseInt(classesFound.get(i));
			Arrays.sort(temp);

			// create classes
			for (int i = 0; i < temp.length; i++) {
				target.getClasses().add(new ClassItem("" + temp[i], i));
			}
		} else {
			// sort string
			String[] temp = new String[classesFound.size()];
			for (int i = 0; i < classesFound.size(); i++)
				temp[i] = classesFound.get(i);
			Arrays.sort(temp);

			// create classes
			for (int i = 0; i < temp.length; i++) {
				target.getClasses().add(new ClassItem(temp[i], i));
			}
		}

		target.init();
		
		this.classify.add(classField, target);
		
		reportDone(true);

	}



	/**
	 * Prepare the output file, write headers if needed.
	 * @param outputFile The name of the output file.
	 * @param originalName The name of original field.
	 * @param idx The index to insert the orig name.
	 * @return The output stream for the text file.
	 */
	public PrintWriter prepareOutputFile(String outputFile) {
		
		try {
			PrintWriter tw = new PrintWriter(new FileWriter(outputFile));
			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				int index = 0;
				StringBuilder line = new StringBuilder();
				for (String str : this.getInputHeadings()) {
					
					for( ClassifyTarget target: this.classify.getFields().values() ) {
					
						String org = target.getOriginalName();
						
					if ( org!=null && index == target.getTargetIndex() ) {
						if (line.length() > 0) {
							line.append(",");
						}

						line.append("\"");
						line.append(org);
						line.append("\"");
					}

					if (line.length() > 0) {
						line.append(",");
					}
					line.append("\"");
					line.append(this.getInputHeadings()[index++]);
					line.append("\"");
				}
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
	public void process(String outputFile) {
		PrintWriter tw;

		validateAnalyzed();

		tw = this.prepareOutputFile(outputFile);
		
		for(ClassifyTarget target: this.classify.getFields().values() ) {
			target.setInserted(false);
		}
		
		ReadCSV csv = new ReadCSV(this.getInputFilename(), this.isExpectInputHeaders(),
				this.getInputFormat());
		this.classify.init();

		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			StringBuilder line = new StringBuilder();
			
			for (int i = 0; i < this.getColumnCount(); i++) {
				if (i > 0) {
					line.append(this.getInputFormat().getSeparator());
				}
				
				boolean shouldInsert = true;
				
				for(ClassifyTarget target: this.classify.getFields().values() ) {
					if (target.getInsertAt() == i) {
						int classNumber = target.lookup(csv
								.get(target.getTargetIndex()));
						
						line.append(target.encode(classNumber));
						line.append(this.getInputFormat().getSeparator());
						target.setInserted(true);
					}	
					
					if (target.getOriginalName() == null && i == target.getTargetIndex()) {
						shouldInsert = false;
					}
				}
				
				if( shouldInsert)
					line.append(csv.get(i));
			}

			// if we failed to insert the class field anywhere, then insert it
			// at the end
			for(ClassifyTarget target: this.classify.getFields().values() ) {
				if (line.charAt(line.length() - 1) != this.getInputFormat().getSeparator())
					line.append(this.getInputFormat().getSeparator());
				int classNumber = target.lookup(csv
						.get(target.getTargetIndex()));
				line.append(target.encode(classNumber));
			}
		

			tw.println(line.toString());
		}

		csv.close();
		tw.close();
		reportDone(false);
		this.classify.init();
	}


}
