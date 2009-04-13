package org.encog.neural.persist.persistors;

import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.generic.Object2XML;
import org.encog.neural.persist.persistors.generic.XML2Object;
import org.encog.parse.ParseTemplate;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;

public class ParseTemplatePersistor implements Persistor {

	@Override
	public EncogPersistedObject load(ReadXML in) {
		ParseTemplate result = new ParseTemplate();
		XML2Object xml = new XML2Object();
		//xml.load(in, result);
		return null;
	}

	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {
		Object2XML xml = new Object2XML();
		xml.save(obj, out);
	}

}
