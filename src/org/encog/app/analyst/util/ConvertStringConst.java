package org.encog.app.analyst.util;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.util.csv.CSVFormat;

/**
 * Convert several Analyst String to the correct object.
 *
 */
public class ConvertStringConst {
	
	public static AnalystFileFormat string2AnalystFileFormat(String str)
	{
		if(str.equalsIgnoreCase("decpnt|comma")) {
			return AnalystFileFormat.DECPNT_COMMA;
		} else if(str.equalsIgnoreCase("decpnt|space")) {
			return AnalystFileFormat.DECPNT_SPACE;
		} else if(str.equalsIgnoreCase("decpnt|semi")) {
			return AnalystFileFormat.DECPNT_SEMI;
		} else if(str.equalsIgnoreCase("decpnt|space")) {
			return AnalystFileFormat.DECCOMMA_SPACE;
		} else if(str.equalsIgnoreCase("decpnt|semi")) {
			return AnalystFileFormat.DECCOMMA_SEMI;
		} else {
			return null;
		}
	}
	
	public static String analystFileFormat2String(AnalystFileFormat af)
	{
		if( af==AnalystFileFormat.DECPNT_COMMA) {
			return "decpnt|comma";
		} else if(af==AnalystFileFormat.DECPNT_SPACE) {
			return "decpnt|space";
		} else if(af==AnalystFileFormat.DECPNT_SEMI) {
			return "decpnt|semi";
		} else if(af==AnalystFileFormat.DECCOMMA_SPACE) {
			return "deccomma|space";
		} else if(af==AnalystFileFormat.DECCOMMA_SEMI) {
			return "deccomma|semi";
		} else {
			return null;
		}
	}
	
	public static CSVFormat convertToCSVFormat(AnalystFileFormat af) {
		if( af==AnalystFileFormat.DECPNT_COMMA) {
			return new CSVFormat('.',',');
		} else if(af==AnalystFileFormat.DECPNT_SPACE) {
			return new CSVFormat('.',' ');
		} else if(af==AnalystFileFormat.DECPNT_SEMI) {
			return new CSVFormat('.',';');
		} else if(af==AnalystFileFormat.DECCOMMA_SPACE) {
			return new CSVFormat(',',' ');
		} else if(af==AnalystFileFormat.DECCOMMA_SEMI) {
			return new CSVFormat(',',';');
		} else {
			return null;
		}
	}
}
