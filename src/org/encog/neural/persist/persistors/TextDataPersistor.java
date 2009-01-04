package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.data.TextData;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.generic.Object2XML;
import org.encog.neural.persist.persistors.generic.XML2Object;
import org.w3c.dom.Element;

public class TextDataPersistor implements Persistor {

	public EncogPersistedObject load(Element node) {
		TextData result = new TextData();
		XML2Object transform = new XML2Object();
		transform.load(node, result);
		return result;
	}

	public void save(EncogPersistedObject object, TransformerHandler hd) {
		Object2XML transform = new Object2XML();
		transform.save(object, hd);
	}

}
