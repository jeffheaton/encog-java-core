package org.encog.app.analyst.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.ClassItem;
import org.encog.app.analyst.script.DataField;

public class AnalyzedField extends DataField {

	private double total;
	private int instances;
	private double devTotal;
	private Map<String, ClassItem> classMap = new HashMap<String, ClassItem>();
	private AnalystScript script;

	public AnalyzedField(AnalystScript script, String name) {
		super(name);
		this.instances = 0;
		this.script = script;
	}

	public void analyze1(String str) {

		if (str.trim().length() == 0) {
			this.setComplete(false);
			return;
		}

		this.instances++;

		if (this.isInteger()) {
			try {
				int i = Integer.parseInt(str);
				this.setMax(Math.max(i, this.getMax()));
				this.setMin(Math.min(i, this.getMin()));
				this.total += i;
			} catch (NumberFormatException ex) {
				this.setInteger(false);
				if (!this.isReal()) {
					this.setMax(0);
					this.setMin(0);
					this.setStandardDeviation(0);
				}
			}
		}

		if (this.isReal()) {
			try {
				double d = Double.parseDouble(str);
				this.setMax(Math.max(d, this.getMax()));
				this.setMin(Math.min(d, this.getMin()));
				this.total += d;
			} catch (NumberFormatException ex) {
				this.setReal(false);
				if (!this.isInteger()) {
					this.setMax(0);
					this.setMin(0);
					this.setStandardDeviation(0);
				}
			}
		}

		if (this.isClass()) {
			if (!this.classMap.containsKey(str)) {
				this.classMap.put(str, new ClassItem(str,str));
			}
			if (this.classMap.size() > script.getConfig().getMaxClassSize())
				this.setClass(false);
		}
	}

	public void completePass1() {

		this.devTotal = 0;

		if (this.instances == 0)
			this.setMean(0);
		else
			this.setMean(this.total / this.instances);
	}

	public void analyze2(String str) {
		if (str.trim().length() == 0) {
			return;
		}

		if (this.isReal() || this.isInteger()) {
			double d = Double.parseDouble(str);
			this.devTotal += Math.pow((d - this.getMean()), 2);
		}
	}

	public void completePass2() {
		this.setStandardDeviation(Math.sqrt(this.devTotal / this.instances));
	}

	public List<ClassItem> getClassMembers() {
		List<String> sorted = new ArrayList<String>();
		sorted.addAll(this.classMap.keySet());		
		Collections.sort(sorted);
		
		List<ClassItem> result = new ArrayList<ClassItem>();
		for(String str: sorted) {
			result.add(this.classMap.get(str));
		}
		
		return result;
	}

	public DataField finalizeField() {
		DataField result = new DataField(this.getName());

		result.setName(getName());
		result.setMin(getMin());
		result.setMax(getMax());
		result.setMean(getMean());
		result.setStandardDeviation(getStandardDeviation());
		result.setInteger(isInteger());
		result.setReal(isReal());
		result.setClass(isClass());
		result.setComplete(isComplete());

		result.getClassMembers().clear();

		if (result.isClass()) {
			List<ClassItem> list = getClassMembers();
			result.getClassMembers().addAll(list);
		}

		return result;
	}
}
