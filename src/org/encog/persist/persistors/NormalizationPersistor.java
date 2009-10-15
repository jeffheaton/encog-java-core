package org.encog.persist.persistors;

import org.encog.normalize.Normalization;
import org.encog.normalize.input.InputField;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.Object2XML;

public class NormalizationPersistor implements Persistor {

	private Normalization currentNormalization;
	
	
	public EncogPersistedObject load(ReadXML in) {
		this.currentNormalization = new Normalization();
		return this.currentNormalization;
	}

	private void saveInputFields()
	{
		for(InputField field: this.currentNormalization.getInputFields())
		{
			
		}
	}
	
	public void save(EncogPersistedObject obj, WriteXML out) {
		this.currentNormalization = (Normalization)obj;		
		Object2XML conv = new Object2XML();
		conv.save(obj, out);		
	}

}
