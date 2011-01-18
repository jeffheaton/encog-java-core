package org.encog.persist.persistors.generic;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.NeuralNetworkError;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.persistors.BasicLayerPersistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.obj.ReflectionUtil;

public class ActivationPersistUtil {
	public static void saveActivationFunction(
			ActivationFunction activationFunction, WriteXML out) {
		if (activationFunction != null) {
			
			String[] names = activationFunction.getParamNames();
			for (int i = 0; i < names.length; i++) {
				String str = names[i];
				double d = activationFunction.getParams()[i];
				out.addAttribute(str, "" + CSVFormat.EG_FORMAT.format(d, 10));
			}
			out.beginTag(activationFunction.getClass().getSimpleName());
			out.endTag();
		}
	}

	public static ActivationFunction loadActivation(String type, ReadXML in) {

		try {
			Class<?> clazz = ReflectionUtil.resolveEncogClass(type);
			
			if( clazz==null ) {
				throw new NeuralNetworkError("Unknown activation function type: " + type);
			}
			
			ActivationFunction result = (ActivationFunction) clazz
					.newInstance();

			for (String key : in.getTag().getAttributes().keySet()) {
				int index = -1;

				for (int i = 0; i < result.getParamNames().length; i++) {
					if (key.equalsIgnoreCase(result.getParamNames()[i])) {
						index = i;
						break;
					}					
				}
				
				if (index != -1) {
					String str = in.getTag().getAttributeValue(key);
					double d = CSVFormat.EG_FORMAT.parse(str);
					result.setParam(index, d);
				}
			}

			return result;
		} catch (InstantiationException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

}
