/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.app.analyst.csv.segregate;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.app.quant.QuantError;
import org.encog.util.Format;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * This class is used to segregate a CSV file into several sub-files. This can
 * be used to create training and evaluation datasets.
 */
public class SegregateCSV extends BasicFile {
	
	/**
	 * TOtal percents should add to this.
	 */
	public static final int TOTAL_PCT = 100;
	
	/**
	 * The segregation targets.
	 */
	private final List<SegregateTargetPercent> targets 
		= new ArrayList<SegregateTargetPercent>();

	/**
	 * Analyze the input file.
	 * 
	 * @param inputFile
	 *            The input file.
	 * @param headers
	 *            The headers.
	 * @param format
	 *            The format of the input file.
	 */
	public final void analyze(final File inputFile, final boolean headers,
			final CSVFormat format) {
		setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);

		performBasicCounts();

		balanceTargets();
	}

	/**
	 * Balance the targets.
	 */
	private void balanceTargets() {
		SegregateTargetPercent smallestItem = null;
		int numberAssigned = 0;

		// first try to assign as many as can be assigned
		for (final SegregateTargetPercent p : this.targets) {
			final SegregateTargetPercent stp = p;

			// assign a number of records to this
			final double percent = stp.getPercent() / Format.HUNDRED_PERCENT;
			final int c = (int) (getRecordCount() * percent);
			stp.setNumberRemaining(c);

			// track the smallest group
			if ((smallestItem == null)
					|| (smallestItem.getPercent() > stp.getPercent())) {
				smallestItem = stp;
			}

			numberAssigned += c;

		}

		// see if there are any remaining items
		final int remain = getRecordCount() - numberAssigned;

		// if there are extras, just add them to the largest group
		if (remain > 0) {
			smallestItem.setNumberRemaining(smallestItem.getNumberRemaining()
					+ remain);
		}
	}

	/**
	 * @return The segregation targets.
	 */
	public final List<SegregateTargetPercent> getTargets() {
		return this.targets;
	}

	/**
	 * Process the input file and segregate into the output files.
	 */
	public final void process() {
		validate();

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());
		resetStatus();
		for (final SegregateTargetPercent target : this.targets) {
			final PrintWriter tw = prepareOutputFile(target.getFilename());

			while ((target.getNumberRemaining() > 0) && csv.next()
					&& !shouldStop()) {
				updateStatus(false);
				final LoadedRow row = new LoadedRow(csv);
				writeRow(tw, row);
				target.setNumberRemaining(target.getNumberRemaining() - 1);
			}

			tw.close();
		}
		reportDone(false);
		csv.close();
	}

	/**
	 * Validate that the data is correct.
	 */
	private void validate() {
		validateAnalyzed();

		if (this.targets.size() < 1) {
			throw new QuantError("There are no segregation targets.");
		}

		if (this.targets.size() < 2) {
			throw new QuantError(
					"There must be at least two segregation targets.");
		}

		int total = 0;
		for (final SegregateTargetPercent p : this.targets) {
			total += p.getPercent();
		}

		if (total != TOTAL_PCT) {
			throw new QuantError("Target percents must equal 100.");
		}
	}
}
