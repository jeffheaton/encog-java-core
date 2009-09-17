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
import java.util.Map;

import org.encog.StatusReportable;
import org.encog.normalize.input.InputField;
import org.encog.normalize.input.InputFieldArray1D;
import org.encog.normalize.input.InputFieldCSV;
import org.encog.normalize.output.OutputField;
import org.encog.normalize.target.NormalizationTarget;
import org.encog.util.ReadCSV;

public class Normalization {

	private final Collection<InputField> inputFields = new ArrayList<InputField>();
	private final Collection<OutputField> outputFields = new ArrayList<OutputField>();
	private final Collection<ReadCSV> readCSV = new ArrayList<ReadCSV>();
	private final Map<InputField, ReadCSV> csvMap = new HashMap<InputField, ReadCSV>();
	private NormalizationTarget target;
	private StatusReportable report;
	private int recordCount;
	private int currentIndex;

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
					ReadCSV csv = new ReadCSV(file.toString(), false, ',');
					uniqueFiles.put(file, csv);
					this.readCSV.add(csv);
				}
				this.csvMap.put(csvField, uniqueFiles.get(file));
			}
		}
	}

	private boolean next() {
		boolean status = true;

		this.currentIndex++;
		
		// see if any of the CSV readers want to stop
		for (ReadCSV csv : this.readCSV) {
			if (!csv.next())
				status = false;
		}
		
		// see if any of the arrays want to stop
		for( InputField field: this.inputFields ) {
			if( field instanceof InputFieldArray1D )
			{
				InputFieldArray1D arrayField = (InputFieldArray1D)field;
				if( this.currentIndex>=arrayField.length() )
					status = false;
			}
		}			

		return status;
	}

	/**
	 * First pass, count everything, establish min/max.
	 */
	private void firstPass() {
		openCSV();
		
		this.currentIndex = -1;
		this.recordCount = 0;

		this.report.report(0, 0, "Analyzing file");
		int report = 0;
		int index = 0;

		// loop over all of the records
		while (next()) {
			// count the records, report status
			this.recordCount++;
			report++;
			if (report >= 10000) {
				this.report.report(0, 0, "Analyzing file, found "
						+ this.recordCount + " records.");
				report = 0;
			}

			// manage the min/max for each field
			for (InputField field : this.inputFields) {
				if (field instanceof InputFieldCSV) {
					InputFieldCSV fieldCSV = (InputFieldCSV) field;
					ReadCSV csv = this.csvMap.get(field);
					double value = csv.getDouble(fieldCSV.getOffset());
					field.applyMinMax(value);
				}
				else
				{
					double value = field.getValue(index);
					field.applyMinMax(value);
				}
			}
			index++;
		}
	}

	private void secondPass() {
		// move any CSV files back to the beginning.
		openCSV();
		this.currentIndex = -1;

		// process the records
		double[] output = new double[this.outputFields.size()];

		this.target.open();
		for (int i = 0; i < this.recordCount; i++) {
			next();
			// read the value
			for (InputField field : this.inputFields) {
				if (field instanceof InputFieldCSV) {
					InputFieldCSV fieldCSV = (InputFieldCSV) field;
					ReadCSV csv = this.csvMap.get(field);
					double value = csv.getDouble(fieldCSV.getOffset());
					field.setCurrentValue(value);
				}
				else
				{
					double value = field.getValue(i);
					field.setCurrentValue(value);
				}
			}

			// write the value
			int outputIndex = 0;
			for (OutputField ofield : this.outputFields) {
				output[outputIndex++] = ofield.calculate();
			}

			this.target.write(output, 0);

			// report status
			this.report.report(this.recordCount, i, "Normalizing data");
		}
		this.target.close();

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
	}

}
