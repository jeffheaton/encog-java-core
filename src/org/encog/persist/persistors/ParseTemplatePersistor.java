package org.encog.persist.persistors;

import org.encog.parse.ParseTemplate;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.Object2XML;
import org.encog.persist.persistors.generic.XML2Object;

public class ParseTemplatePersistor implements Persistor {

	public EncogPersistedObject load(ReadXML in) {
		ParseTemplate result = new ParseTemplate();
		XML2Object xml = new XML2Object();
		//xml.load(in, result);
		return null;
	}

	public void save(EncogPersistedObject obj, WriteXML out) {
		Object2XML xml = new Object2XML();
		xml.save(obj, out);
	}

}
