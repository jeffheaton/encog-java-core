package org.encog.neural.networks.training.propagation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;

public class PersistTrainingContinuation implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "TrainingContinuation";
	}

	@Override
	public Object read(InputStream is) {
		TrainingContinuation result = new TrainingContinuation();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("CONT")
					&& section.getSubSectionName().equals("PARAMS")) {
				Map<String, String> params = section.parseParams();
				for (String key : params.keySet()) {
					if (key.equalsIgnoreCase("type")) {
						result.setTrainingType(params.get(key));
					} else {
						double[] list = EncogFileSection.parseDoubleArray(
								params, key);
						result.put(key, list);
					}
				}
			}
		}

		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		TrainingContinuation cont = (TrainingContinuation) obj;
		out.addSection("CONT");
		out.addSubSection("PARAMS");
		out.writeProperty("type", cont.getTrainingType());
		for (String key : cont.getContents().keySet()) {
			double[] list = (double[]) cont.get(key);
			out.writeProperty(key, list);
		}
		out.flush();
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
