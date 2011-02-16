package org.encog.app.quant.classify;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.quant.QuantError;
import org.encog.engine.util.EngineArray;
import org.encog.mathutil.Equilateral;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Holds stats about a field that has been classified.
 */
public class ClassifyStats {

	private Map<Integer, ClassifyTarget> fields = new HashMap<Integer, ClassifyTarget>();
	private CSVFormat format;
	
	/**
	 * The desired precision when numbers must be written. Defaults to 10
	 * decimal places.
	 */
	public int precision = 10;
	

	public ClassifyTarget getField(int field) {
		if (this.fields.containsKey(field)) {
			return this.fields.get(field);
		}

		return null;
	}

	public ClassifyTarget findTarget(int field) {
		if (this.fields.containsKey(field)) {
			return this.fields.get(field);
		}

		ClassifyTarget target = new ClassifyTarget(this);
		this.fields.put(field, target);
		return target;
	}

	/**
	 * Read the stats file from a CSV.
	 * @param filename The filename to read.
	 */
	public void readStatsFile(String filename) {
		ReadCSV csv = null;

		try {
			this.fields.clear();
			csv = new ReadCSV(filename, true, CSVFormat.EG_FORMAT);
			while (csv.next()) {
				int fieldName = Integer.parseInt(csv.get(0));
				String name = csv.get(1);
				int index = Integer.parseInt(csv.get(2));
				int targetIndex = Integer.parseInt(csv.get(3));
				String method = csv.get(4);
				double high = csv.getDouble(5);
				double low = csv.getDouble(6);

				ClassifyTarget target = this.findTarget(fieldName);
				ClassItem item = new ClassItem(name, index);

				target.add(item);
				target.setTargetIndex(targetIndex);
				target.setHigh(high);
				target.setLow(low);

				if (method.equals("o")) {
					target.setMethod(ClassifyMethod.OneOf);
				} else if (method.equals("e")) {
					target.setMethod(ClassifyMethod.Equilateral);
				} else if (method.equals("s")) {
					target.setMethod(ClassifyMethod.SingleField);
				}
			}
			csv.close();
			init();

		} finally {
			if (csv != null)
				csv.close();
		}
	}

	public void init() {
		for (int targetName : this.fields.keySet()) {
			ClassifyTarget target = this.fields.get(targetName);
			target.init();
		}
	}

	/**
	 * Write the stats file.
	 * @param filename The filename to write.
	 */
	public void writeStatsFile(String filename) {
		PrintWriter tw = null;

		try {
			tw = new PrintWriter(new FileWriter(filename));

			tw.println("field,name,index,field,method,high,low");

			for (int targetName : this.fields.keySet()) {
				ClassifyTarget target = this.fields.get(targetName);

				for (ClassItem item : target.getClasses() ) {
					StringBuilder line = new StringBuilder();

					line.append("\"");
					line.append(targetName);
					line.append("\",");
					line.append(item.getName());
					line.append("\",");
					line.append(item.getIndex());
					line.append(",");
					line.append(target.getTargetIndex());
					line.append(",");

					switch (target.getMethod()) {
					case Equilateral:
						line.append("e");
						break;
					case OneOf:
						line.append("o");
						break;
					case SingleField:
						line.append("s");
						break;
					}

					line.append(',');
					line.append(target.getHigh());
					line.append(',');
					line.append(target.getLow());

					tw.println(line.toString());
				}
			}
		} catch (IOException e) {
			throw new QuantError(e);
		} finally {
			// close the stream
			if (tw != null)
				tw.close();
		}
	}

	/**
	 * @return the format
	 */
	public CSVFormat getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(CSVFormat format) {
		this.format = format;
	}

	public void add(int classField, ClassifyTarget target) {
		this.fields.put(classField, target);
		
	}

	public Map<Integer, ClassifyTarget> getFields() {
		return this.fields;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
	

}
