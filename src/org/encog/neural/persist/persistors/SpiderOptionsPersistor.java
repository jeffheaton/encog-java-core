package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.bot.spider.SpiderOptions;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.generic.Object2XML;
import org.encog.neural.persist.persistors.generic.XML2Object;
import org.w3c.dom.Element;

public class SpiderOptionsPersistor implements Persistor {
	
	
	public EncogPersistedObject load(Element node) {
		SpiderOptions result = new SpiderOptions();
		XML2Object xml2obj = new XML2Object();
		xml2obj.load(node, result);
		return result;
	}

	public void save(EncogPersistedObject object, TransformerHandler hd) {
		Object2XML obj2xml = new Object2XML();
		obj2xml.save(object, hd);
	}
}
