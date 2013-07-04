package org.encog.app.analyst.util;

import java.io.File;

import org.encog.EncogError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.TimeSeriesUtil;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;

public class AnalystUtility {
	final private EncogAnalyst analyst;
	
	public AnalystUtility(EncogAnalyst theAnalyst) {
		this.analyst = theAnalyst;
	}
	
	public void prepareInput(double[] rawInput, MLData preparedInput) {
		int rawIndex = 0;
		int outputIndex = 0;
		
		for (final AnalystField stat : analyst.getScript().getNormalize()
				.getNormalizedFields()) {

			if (stat.getAction() == NormalizationAction.Ignore) {
				continue;
			}
			
			if (stat.isOutput() ) {
				continue;
			}
			
			if (stat.getAction() == NormalizationAction.Normalize) {
				preparedInput.setData(outputIndex++, stat.normalize(rawInput[rawIndex++]));
			} else if (stat.getAction() == NormalizationAction.PassThrough) {
				preparedInput.setData(outputIndex++, rawInput[rawIndex++]);
			} else {
				final double[] d = stat.encode(rawInput[rawIndex++]);
				for (final double element : d) {
					preparedInput.setData(outputIndex++, element);
				}
			}
		}
	}
	
	public MLDataSet loadCSV(File file) {
		if (this.analyst == null) {
			throw new EncogError(
					"Can't normalize yet, file has not been analyzed.");
		}
		
		MLDataSet result = new BasicMLDataSet();
		
		int inputCount = this.analyst.determineInputCount();
		int outputCount = this.analyst.determineOutputCount();
		int totalCount = inputCount+outputCount;
		
		boolean headers = this.analyst.getScript().getProperties()
				.getPropertyBoolean(ScriptProperties.SETUP_CONFIG_INPUT_HEADERS);
		
		final CSVFormat format = this.analyst.getScript().determineFormat();

		CSVHeaders analystHeaders = new CSVHeaders(file, headers,
				format);
		
		ReadCSV csv = new ReadCSV(file.toString(), headers, format);
		
		for (final AnalystField field : analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			field.init();
		}

		TimeSeriesUtil series = new TimeSeriesUtil(analyst,true,
				analystHeaders.getHeaders());
		

		try {
			// write file contents
			while (csv.next()) {

				double[] output = AnalystNormalizeCSV.extractFields(
						this.analyst, analystHeaders, csv, totalCount,
						false);

				if (series.getTotalDepth() > 1) {
					output = series.process(output);
				}

				MLDataPair pair = BasicMLDataPair.createPair(inputCount,outputCount);
				for(int i=0;i<inputCount;i++) {
					pair.getInput().setData(i, output[i]);
				}
				for(int i=0;i<outputCount;i++) {
					pair.getIdeal().setData(i, output[i+inputCount]);
				}
				result.add(pair);
			}
			return result;
		} finally {
			if (csv != null) {
				try {
					csv.close();
				} catch (final Exception ex) {
					EncogLogging.log(ex);
				}
			}
		}
	}

	public MLDataSet loadCSV(String filename) {
		return loadCSV(new File(filename));
	}
	
	
}
