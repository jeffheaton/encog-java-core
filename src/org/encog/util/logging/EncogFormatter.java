package org.encog.util.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class EncogFormatter extends Formatter {

	public String format(LogRecord record) {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(record.getLevel());
		result.append("] [");
		result.append(record.getSourceClassName());
		result.append("] ");
		result.append(record.getMessage());
		result.append("\n");
		return result.toString();
	}

}
