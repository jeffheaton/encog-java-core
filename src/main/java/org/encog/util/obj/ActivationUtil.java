package org.encog.util.obj;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public final class ActivationUtil {
	public static String generateActivationFactory(String name, ActivationFunction af) {
		StringBuilder result = new StringBuilder();
		
		result.append(name.toUpperCase());
		
		if( af.getParams()!=null && af.getParams().length>0 ) {
			result.append('[');			
			NumberList.toList(CSVFormat.EG_FORMAT, result, af.getParams());			
			result.append(']');
		}
		
		return result.toString();
	}
}
