package org.encog.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.encog.app.analyst.AnalystError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.matrices.Matrix;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class EncogFileSection {
	
	private final String sectionName;
	private final String subSectionName;
	private final List<String> lines = new ArrayList<String>();
	
	public EncogFileSection(String sectionName, String subSectionName) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
	}
	
	public String getSectionName() {
		return sectionName;
	}
	
	public String getSubSectionName() {
		return subSectionName;
	}
	
	public List<String> getLines() {
		return lines;
	}

	public Map<String, String> parseParams() {
		Map<String,String> result = new HashMap<String,String>();

		for(String line: this.lines) {
			line = line.trim();
			if(line.length()>0 ) {
				int idx = line.indexOf('=');
				if( idx==-1 ) {
					throw new AnalystError("Invalid setup item: " + line);
				}
				String name = line.substring(0,idx).trim();
				String value = line.substring(idx+1).trim();
								
				result.put(name, value);
			}
		}
		
		return result;
	}
	
	public static int parseInt(Map<String,String> params, String name) {
		String value = null;
		try {
			value = params.get(name);
			if( value==null ) {
				throw new PersistError("Missing property: " + name);
			}
			
			return Integer.parseInt(value);
			
		} catch(NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", " + "invalid integer: " + value);
		}
	}
	
	public static double parseDouble(Map<String,String> params, String name) {
		String value = null;
		try {
			value = params.get(name);
			if( value==null ) {
				throw new PersistError("Missing property: " + name);
			}
			
			return CSVFormat.EG_FORMAT.parse(value);
			
		} catch(NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", " + "invalid integer: " + value);
		}
	}

	public static Matrix parseMatrix(Map<String, String> params,
			String name) {
		
		if( !params.containsKey(name) ) {
			throw new PersistError("Missing property: " + name );
		}
		
		String line = params.get(name);

		double[] d = NumberList.fromList(CSVFormat.EG_FORMAT, line);
		int rows = (int)d[0];
		int cols = (int)d[1];
		
		Matrix result = new Matrix(rows,cols);
		
		int index = 2;
		for(int r = 0;r<rows;r++)
		{
			for(int c = 0; c< cols; c++)
			{
				result.set(r,c,d[index++]);
			}
		}
		
		return result;
	}

	public static int[] parseIntArray(Map<String, String> params,
			String name) {
		String value = null;
		try {
			value = params.get(name);
			if( value==null ) {
				throw new PersistError("Missing property: " + name);
			}
			
			return NumberList.fromListInt(CSVFormat.EG_FORMAT, value);
			
		} catch(NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", " + "invalid integer: " + value);
		}
	}
	
	public static double[] parseDoubleArray(Map<String, String> params,
			String name) {
		String value = null;
		try {
			value = params.get(name);
			if( value==null ) {
				throw new PersistError("Missing property: " + name);
			}
			
			return NumberList.fromList(CSVFormat.EG_FORMAT, value);
			
		} catch(NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", " + "invalid integer: " + value);
		}
	}

	public static boolean parseBoolean(Map<String, String> params,
			String name) {
		String value = null;
		try {
			value = params.get(name);
			if( value==null ) {
				throw new PersistError("Missing property: " + name);
			}
			
			return value.trim().toLowerCase().charAt(0) == 't';
			
		} catch(NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", " + "invalid integer: " + value);
		}
	}
	
	public static List<String> splitColumns(String line) {
		List<String> result = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(line,",");
		while(tok.hasMoreTokens()) {
			String str = tok.nextToken().trim();
			if( str.length()>0 && str.charAt(0)=='\"')
			{
				str = str.substring(1);
				if( str.endsWith("\""))
				{
					str = str.substring(0,str.length()-1);
				}
			}
			result.add(str);
		}
		return result;
	}

	public static ActivationFunction parseActivationFunction(
			Map<String, String> params, String name) {
		String value = null;
		try {
			value = params.get(name);
			if( value==null ) {
				throw new PersistError("Missing property: " + name);
			}
			
			ActivationFunction af = null;
			String[] cols = value.split("\\|");
			
			String afName = "org.encog.engine.network.activation." + cols[0];
			try {
				Class<?> clazz = Class.forName(afName);
				af = (ActivationFunction) clazz.newInstance();
			} catch (ClassNotFoundException e) {
				throw new PersistError(e);
			} catch (InstantiationException e) {
				throw new PersistError(e);
			} catch (IllegalAccessException e) {
				throw new PersistError(e);
			}
			
			for(int i=0;i<af.getParamNames().length;i++) {
				af.setParam(i, CSVFormat.EG_FORMAT.parse(cols[i+1]));
			}
			
			return af;
			
		} catch(Exception ex) {
			throw new PersistError(ex);
		}
	}

	public String getLinesAsString() {
		StringBuilder result = new StringBuilder();
		for(String line : this.lines) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}

	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" sectionName=");
		result.append(this.sectionName);
		result.append(", subSectionName=");
		result.append(this.subSectionName);
		result.append("]");
		return result.toString();
	}
	
}
