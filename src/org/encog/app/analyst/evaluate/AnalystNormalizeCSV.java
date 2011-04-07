package org.encog.app.analyst.evaluate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.EncogError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Normalize, or denormalize, a CSV file.
 *
 */
public class AnalystNormalizeCSV extends BasicFile {

	private EncogAnalyst analyst;
	
	/**
	 * Set the source file.  This is useful if you want to use pre-existing stats 
	 * to normalize something and skip the analyze step.
	 * @param file The file to use.
	 * @param headers True, if headers are to be expected.
	 * @param format The format of the CSV file.
	 */
	public void setSourceFile(File file, boolean headers, CSVFormat format) {
		this.setInputFilename(file);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);
	}



	/**
	 * Write the headers.
	 * @param tw The output stream.
	 */
	private void writeHeaders(PrintWriter tw) {
		StringBuilder line = new StringBuilder();
		for (AnalystField stat : this.analyst.getScript().getNormalize().getNormalizedFields()) {
			if (line.length() > 0
					&& stat.getAction() != NormalizationAction.Ignore)
				line.append(this.getInputFormat().getSeparator());

			int needed = stat.getColumnsNeeded();
			
			for(int i=0;i<needed;i++) {
				line.append(this.tagColumn(stat.getName(), i, 0, needed>1));
			}
		}
		tw.println(line.toString());
	}

	/**
	 * Normalize the input file.  Write to the specified file.
	 * @param file The file to write to.
	 */
	public void normalize(File file) {
		if (this.analyst==null)
			throw new EncogError(
					"Can't normalize yet, file has not been analyzed.");

		ReadCSV csv = null;
		PrintWriter tw = null;

		try {
			csv = new ReadCSV(this.getInputFilename().toString(),
					this.isExpectInputHeaders(), this.getInputFormat());

			tw = new PrintWriter(new FileWriter(file));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				writeHeaders(tw);
			}

			resetStatus();
			// write file contents
			while (csv.next()&& !this.shouldStop()) {
				StringBuilder line = new StringBuilder();
				updateStatus(false);
				int index = 0;
				for (AnalystField stat : this.analyst.getScript().getNormalize().getNormalizedFields()) {
					String str = csv.get(index++);
					if (line.length() > 0
							&& stat.getAction() != NormalizationAction.Ignore)
						line.append(this.getInputFormat().getSeparator());
					switch (stat.getAction()) {
					case PassThrough:
						line.append("\"");
						line.append(str);
						line.append("\"");
						break;
					case Normalize:
						try {
							double d = this.getInputFormat().parse(str);
							d = stat.normalize(d);
							line.append(this.getInputFormat().format(d,
									this.getPrecision()));
						} catch (NumberFormatException ex) {

						}
						break;

					case OneOf:
						line.append(stat.encode(str));
						break;
					case SingleField:
						line.append(stat.encode(str));
						break;
					case Equilateral:
						line.append(stat.encode(str));
						break;
					}
				}
				tw.println(line);
			}
		} catch (IOException e) {
			throw new QuantError(e);
		} finally {
			reportDone(false);
			if (csv != null) {
				try {
					csv.close();
				} catch (Exception ex) {
				}
			}

			if (tw != null) {
				try {
					tw.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public void analyze(File inputFilename, boolean expectInputHeaders,
			CSVFormat inputFormat, EncogAnalyst analyst) {
		this.inputFilename = inputFilename;
		this.inputFormat = inputFormat;
		this.expectInputHeaders = expectInputHeaders;
		this.analyst = analyst;
		this.analyzed = true;
		
		for(AnalystField field: analyst.getScript().getNormalize().getNormalizedFields())
		{
			field.init(this.analyst);
		}
	}
	
	public String tagColumn(String name, int part, int timeSlice, boolean multiPart) {
		StringBuilder result = new StringBuilder();
		result.append(name);
		
		// is there any suffix?
		if( multiPart || timeSlice!=0 ) {
			result.append('(');
			
			// is there a part?
			if( multiPart ) {
				result.append('p');
				result.append(part);
			}
			
			// is there a timeslice?
			if( timeSlice!=0) {
				if( multiPart )
					result.append(',');
				result.append('t');
				if(timeSlice>0)
					result.append('+');
				result.append(timeSlice);
				
			}
			
			
			result.append(')');
		}
		return result.toString();
	}

}
