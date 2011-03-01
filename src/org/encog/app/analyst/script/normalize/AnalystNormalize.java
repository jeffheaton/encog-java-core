package org.encog.app.analyst.script.normalize;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizedField;

public class AnalystNormalize {
	
	private NormalizedField[] normalizedFields;
	

	/**
	 * @return the normalizedFields
	 */
	public NormalizedField[] getNormalizedFields() {
		return normalizedFields;
	}

	/**
	 * @param normalizedFields the normalizedFields to set
	 */
	public void setNormalizedFields(NormalizedField[] normalizedFields) {
		this.normalizedFields = normalizedFields;
	}

	public int calculateInputColumns(NormalizedField targetField) {
		int result = 0;
		for( NormalizedField field: this.normalizedFields ) {
			if( field!=targetField )
			result+=field.getColumnsNeeded();
		}
		return result;
	}

	public int calculateOutputColumns(NormalizedField targetField) {
		return targetField.getColumnsNeeded();
	}

	public void init(AnalystScript script) {
		for( NormalizedField norm : this.normalizedFields ) {
			DataField f = script.findDataField(norm.getName());
			
			if( f==null ) {
				throw new AnalystError("Normalize specifies unknown field: " + norm.getName());
			}

			if( norm.getAction()==NormalizationAction.Normalize) {
				norm.setActualHigh(f.getMax());
				norm.setActualLow(f.getMin());
			}
			
			if( norm.getAction()==NormalizationAction.Equilateral ||
				norm.getAction()==NormalizationAction.OneOf ||
				norm.getAction()==NormalizationAction.SingleField ) {
								
				int index = 0;
				for(AnalystClassItem item : f.getClassMembers() )
				{
					norm.getClasses().add(new ClassItem(item.getName(),index++));
				}				
			}			
		}		
	}
}
