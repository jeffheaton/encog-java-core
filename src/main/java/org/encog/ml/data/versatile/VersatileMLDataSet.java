package org.encog.ml.data.versatile;

import java.util.List;

import org.encog.EncogError;
import org.encog.mathutil.randomize.generate.GenerateRandom;

public class VersatileMLDataSet extends MatrixMLDataSet {

	private VersatileDataSource source;
	private NormalizationHelper helper = new NormalizationHelper();

	private int analyzedRows;

	public VersatileMLDataSet(VersatileDataSource theSource) {
		this.source = theSource;
	}

	public void analyze() {
		String[] line;

		// Collect initial stats: sums (for means), highs, lows.
		this.source.rewind();
		int c = 0;
		while ((line = this.source.readLine()) != null) {
			c++;
			for (int i = 0; i < this.helper.getSourceColumns().size(); i++) {
				ColumnDefinition colDef = this.helper.getSourceColumns().get(i);
				String value = line[i];
				colDef.analyze(value);
			}
		}
		this.analyzedRows = c;

		// Calculate the means, and reset for sd calc.
		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			// Only calculate mean/sd for continuous columns.
			if (colDef.getDataType() == ColumnType.continuous) {
				colDef.setMean(colDef.getMean() / colDef.getCount());
				colDef.setSd(0);
			}
		}

		// Sum the standard deviation
		this.source.rewind();
		while ((line = this.source.readLine()) != null) {
			for (int i = 0; i < this.helper.getSourceColumns().size(); i++) {
				ColumnDefinition colDef = this.helper.getSourceColumns().get(i);
				String value = line[i];
				if (colDef.getDataType() == ColumnType.continuous) {
					double d = this.helper.parseDouble(value);
					d = colDef.getMean() - d;
					d = d * d;
					colDef.setSd(colDef.getSd() + d);
				}
			}
		}

		// Calculate the standard deviations.
		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			// Only calculate sd for continuous columns.
			if (colDef.getDataType() == ColumnType.continuous) {
				colDef.setSd(Math.sqrt(colDef.getSd() / colDef.getCount()));
			}
		}
	}

	public void normalize() {
		NormalizationStrategy strat = this.helper.getNormStrategy();
		
		if( strat==null ) {
			throw new EncogError("Please choose a model type first, with selectMethod.");
		}
		
		int normalizedRows = strat.calculateTotalRows(this.analyzedRows);
		int normalizedInputColumns = this.helper
				.calculateNormalizedInputCount();
		int normalizedOutputColumns = this.helper
				.calculateNormalizedOutputCount();

		int normalizedColumns = normalizedInputColumns
				+ normalizedOutputColumns;
		setCalculatedIdealSize(normalizedOutputColumns);
		setCalculatedInputSize(normalizedInputColumns);

		setData(new double[normalizedRows][normalizedColumns]);

		this.source.rewind();
		String[] line;
		int row = 0;
		while ((line = this.source.readLine()) != null) {
			int column = 0;
			for (ColumnDefinition colDef : this.helper.getInputColumns()) {
				int index = this.helper.getSourceColumns().indexOf(colDef);
				String value = line[index];
				
				column = this.helper.normalizeToVector(colDef, column, getData()[row], true, value);
			}

			for (ColumnDefinition colDef : this.helper.getOutputColumns()) {
				int index = this.helper.getSourceColumns().indexOf(colDef);
				String value = line[index];
				
				column = this.helper.normalizeToVector(colDef, column, getData()[row], false, value);
			}
			row++;
		}
	}

	public ColumnDefinition defineSourceColumn(String name, ColumnType colType) {
		return this.helper.defineSourceColumn(name, colType);
	}

	/**
	 * @return the helper
	 */
	public NormalizationHelper getNormHelper() {
		return helper;
	}

	/**
	 * @param helper
	 *            the helper to set
	 */
	public void setNormHelper(NormalizationHelper helper) {
		this.helper = helper;
	}

	public void divide(List<DataDivision> dataDivisionList, boolean shuffle,
			GenerateRandom rnd) {
		if( getData()==null ) {
			throw new EncogError("Can't divide, data has not yet been generated/normalized.");
		}
		
		PerformDataDivision divide = new PerformDataDivision(shuffle, rnd);
		divide.perform(dataDivisionList, getData(), getCalculatedInputSize(),
				getCalculatedIdealSize());

	}

	public void defineSingleOutputOthersInput(ColumnDefinition outputColumn) {
		this.helper.clearInputOutput();

		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			if (colDef == outputColumn) {
				this.helper.getOutputColumns().add(colDef);
			} else if(colDef.getDataType()!=ColumnType.ignore) {
				this.helper.getInputColumns().add(colDef);
			}
		}
	}

}
