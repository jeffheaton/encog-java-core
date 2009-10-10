/**
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.normalize;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.encog.StatusReportable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.normalize.input.HasFixedLength;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldCSV;
import org.encog.normalize.input.InputFieldNeuralDataSet;
import org.encog.normalize.input.NeuralDataFieldHolder;
import org.encog.normalize.output.OutputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;
import org.encog.normalize.segregate.Segregator;
import org.encog.normalize.target.NormalizationTarget;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class Normalization {

	private final Collection<InputField> inputFields = new ArrayList<InputField>();
	private final Collection<OutputField> outputFields = new ArrayList<OutputField>();
	private final Collection<ReadCSV> readCSV = new ArrayList<ReadCSV>();
	private final Map<InputField, ReadCSV> csvMap = new HashMap<InputField, ReadCSV>();
	private final Collection<Iterator<NeuralDataPair>> readDataSet = new ArrayList<Iterator<NeuralDataPair>>();
	private final Map<InputField, NeuralDataFieldHolder> dataSetFieldMap = new HashMap<InputField, NeuralDataFieldHolder>();
	private final Map<Iterator<NeuralDataPair>, NeuralDataFieldHolder> dataSetIteratorMap = new HashMap<Iterator<NeuralDataPair>, NeuralDataFieldHolder>();
	private final Set<OutputFieldGroup> groups = new HashSet<OutputFieldGroup>();
	private final Collection<Segregator> segregators = new ArrayList<Segregator>();
	private NormalizationTarget target;
	private StatusReportable report;
	private int recordCount;
	private int currentIndex;
	private CSVFormat csvFormat = CSVFormat.ENGLISH;
	private int lastReport;

	public NormalizationTarget getTarget() {
		return target;
	}

	public void setTarget(NormalizationTarget target) {
		this.target = target;
	}

	public StatusReportable getReport() {
		return report;
	}

	public void setReport(StatusReportable report) {
		this.report = report;
	}

	public Collection<InputField> getInputFields() {
		return inputFields;
	}

	public Collection<OutputField> getOutputFields() {
		return outputFields;
	}

	public void addSegregator(Segregator segregator) {
		this.segregators.add(segregator);
		segregator.init(this);
	}

	private void openCSV() {
		// clear out any CSV files already there
		this.csvMap.clear();
		this.readCSV.clear();

		// only add each CSV once
		Map<File, ReadCSV> uniqueFiles = new HashMap<File, ReadCSV>();

		// find the unique files
		for (InputField field : this.inputFields) {
			if (field instanceof InputFieldCSV) {
				InputFieldCSV csvField = (InputFieldCSV) field;
				File file = csvField.getFile();
				if (!uniqueFiles.containsKey(file)) {
					ReadCSV csv = new ReadCSV(file.toString(), false,
							this.csvFormat);
					uniqueFiles.put(file, csv);
					this.readCSV.add(csv);
				}
				this.csvMap.put(csvField, uniqueFiles.get(file));
			}
		}
	}

	private void openDataSet() {
		// clear out any data sets already there
		this.readDataSet.clear();
		this.dataSetFieldMap.clear();
		this.dataSetIteratorMap.clear();

		// only add each iterator once
		Map<NeuralDataSet, NeuralDataFieldHolder> uniqueSets = new HashMap<NeuralDataSet, NeuralDataFieldHolder>();

		// find the unique files
		for (InputField field : this.inputFields) {
			if (field instanceof InputFieldNeuralDataSet) {
				InputFieldNeuralDataSet dataSetField = (InputFieldNeuralDataSet) field;
				NeuralDataSet dataSet = dataSetField.getNeuralDataSet();
				if (!uniqueSets.containsKey(dataSet)) {
					Iterator<NeuralDataPair> iterator = dataSet.iterator();
					NeuralDataFieldHolder holder = new NeuralDataFieldHolder(
							iterator, dataSetField);
					uniqueSets.put(dataSet, holder);
					this.readDataSet.add(iterator);
				}

				NeuralDataFieldHolder holder = uniqueSets.get(dataSet);

				this.dataSetFieldMap.put(dataSetField, holder);
				this.dataSetIteratorMap.put(holder.getIterator(), holder);
			}
		}
	}

	private boolean next() {

		// see if any of the CSV readers want to stop
		for (ReadCSV csv : this.readCSV) {
			if (!csv.next())
				return false;
		}

		// see if any of the data sets want to stop
		for (Iterator<NeuralDataPair> iterator : this.readDataSet) {
			if (!iterator.hasNext())
				return false;
			NeuralDataFieldHolder holder = this.dataSetIteratorMap
					.get(iterator);
			NeuralDataPair pair = iterator.next();
			holder.setPair(pair);
		}

		// see if any of the arrays want to stop
		for (InputField field : this.inputFields) {
			if (field instanceof HasFixedLength) {
				HasFixedLength fixed = (HasFixedLength) field;
				if ((this.currentIndex + 1) >= fixed.length())
					return false;
			}
		}

		this.currentIndex++;

		return true;
	}

	private void reportResult(String message, int total, int current) {
		// count the records, report status
		this.lastReport++;
		if (lastReport >= 10000) {
			this.report.report(total, current, message);
			this.lastReport = 0;
		}
	}

	private double determineInputFieldValue(InputField field, int index) {
		double result = 0;

		if (field instanceof InputFieldCSV) {
			InputFieldCSV fieldCSV = (InputFieldCSV) field;
			ReadCSV csv = this.csvMap.get(field);
			result = csv.getDouble(fieldCSV.getOffset());
		} else if (field instanceof InputFieldNeuralDataSet) {
			InputFieldNeuralDataSet neuralField = (InputFieldNeuralDataSet) field;
			NeuralDataFieldHolder holder = this.dataSetFieldMap.get(field);
			NeuralDataPair pair = holder.getPair();
			int offset = neuralField.getOffset();
			if (offset < pair.getInput().size()) {
				result = pair.getInput().getData(offset);
			} else {
				offset -= pair.getInput().size();
				result = pair.getIdeal().getData(offset);
			}
		} else {
			result = field.getValue(index);
		}

		field.setCurrentValue(result);
		return result;
	}
	
	private void determineInputFieldValues(int index)
	{
		for (InputField field : this.inputFields) {
			this.determineInputFieldValue(field, index);
		}
	}
	
	private void applyMinMax()
	{
		for (InputField field : this.inputFields) {
			double value = field.getCurrentValue();
			field.applyMinMax(value);
		}
	}

	/**
	 * First pass, count everything, establish min/max.
	 */
	private void firstPass() {
		openCSV();
		openDataSet();

		this.currentIndex = -1;
		this.recordCount = 0;

		this.report.report(0, 0, "Analyzing file");
		this.lastReport = 0;
		int index = 0;

		// loop over all of the records
		while (next()) {
			
			this.determineInputFieldValues(index);

			if (this.shouldInclude()) {
				applyMinMax();
				this.recordCount++;
				this.reportResult("First pass, analyzing file", 0,
						this.recordCount);
			}
			index++;
		}
	}

	private boolean shouldInclude() {
		for (Segregator segregator : this.segregators) {
			if (!segregator.shouldInclude()) {
				return false;
			}
		}
		return true;
	}

	private void secondPass() {
		// move any CSV and datasets files back to the beginning.
		openCSV();
		openDataSet();

		this.currentIndex = -1;

		// process the records
		double[] output = new double[this.outputFields.size()];

		this.target.open();
		this.lastReport = 0;
		int index = 0;
		int current = 0;
		while (next()) {
			// read the value
			for (InputField field : this.inputFields) {
				this.determineInputFieldValue(field, index);
			}

			if (shouldInclude()) {
				// handle groups
				initGroups();

				// write the value
				int outputIndex = 0;
				for (OutputField ofield : this.outputFields) {
					output[outputIndex++] = ofield.calculate();
				}

				this.reportResult("Second pass, normalizing data",
						this.recordCount, ++current);
				this.target.write(output, 0);
			}

			index++;
		}
		this.target.close();

	}

	private void initGroups() {
		for (OutputFieldGroup group : this.groups) {
			group.rowInit();
		}
	}

	public void process() {
		firstPass();
		secondPass();
	}

	public void addInputField(InputField f) {
		this.inputFields.add(f);
	}

	public void addOutputField(OutputField outputField) {
		this.outputFields.add(outputField);
		if (outputField instanceof OutputFieldGrouped) {
			OutputFieldGrouped ofg = (OutputFieldGrouped) outputField;
			this.groups.add(ofg.getGroup());
		}
	}

	public Set<OutputFieldGroup> getGroups() {
		return groups;
	}

	public Collection<Segregator> getSegregators() {
		return segregators;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public CSVFormat getCSVFormat() {
		return csvFormat;
	}

	public void setCSVFormat(CSVFormat csvFormat) {
		this.csvFormat = csvFormat;
	}

}
