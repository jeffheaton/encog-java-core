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
package org.encog.app.analyst.util;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.util.csv.CSVFormat;

/**
 * Convert several Analyst String to the correct object.
 * 
 */
public final class ConvertStringConst {
	
	/**
	 * Private constructor.
	 */
	private ConvertStringConst() {
		
	}

	/**
	 * Convert a file format to a string.
	 * @param af The file format.
	 * @return A string.
	 */
	public static String analystFileFormat2String(final AnalystFileFormat af) {
		if (af == AnalystFileFormat.DECPNT_COMMA) {
			return "decpnt|comma";
		} else if (af == AnalystFileFormat.DECPNT_SPACE) {
			return "decpnt|space";
		} else if (af == AnalystFileFormat.DECPNT_SEMI) {
			return "decpnt|semi";
		} else if (af == AnalystFileFormat.DECCOMMA_SPACE) {
			return "deccomma|space";
		} else if (af == AnalystFileFormat.DECCOMMA_SEMI) {
			return "deccomma|semi";
		} else {
			return null;
		}
	}

	/**
	 * Convert an analyst format to a csv format.
	 * @param af The analyst format.
	 * @return The CSV format.
	 */
	public static CSVFormat convertToCSVFormat(final AnalystFileFormat af) {
		if (af == AnalystFileFormat.DECPNT_COMMA) {
			return new CSVFormat('.', ',');
		} else if (af == AnalystFileFormat.DECPNT_SPACE) {
			return new CSVFormat('.', ' ');
		} else if (af == AnalystFileFormat.DECPNT_SEMI) {
			return new CSVFormat('.', ';');
		} else if (af == AnalystFileFormat.DECCOMMA_SPACE) {
			return new CSVFormat(',', ' ');
		} else if (af == AnalystFileFormat.DECCOMMA_SEMI) {
			return new CSVFormat(',', ';');
		} else {
			return null;
		}
	}

	/**
	 * Convert a string to an analyst file format.
	 * @param str The string.
	 * @return The analyst file format.
	 */
	public static AnalystFileFormat string2AnalystFileFormat(final String str) {
		if (str.equalsIgnoreCase("decpnt|comma")) {
			return AnalystFileFormat.DECPNT_COMMA;
		} else if (str.equalsIgnoreCase("decpnt|space")) {
			return AnalystFileFormat.DECPNT_SPACE;
		} else if (str.equalsIgnoreCase("decpnt|semi")) {
			return AnalystFileFormat.DECPNT_SEMI;
		} else if (str.equalsIgnoreCase("deccomma|space")) {
			return AnalystFileFormat.DECCOMMA_SPACE;
		} else if (str.equalsIgnoreCase("deccomma|semi")) {
			return AnalystFileFormat.DECCOMMA_SEMI;
		} else {
			return null;
		}
	}
}
