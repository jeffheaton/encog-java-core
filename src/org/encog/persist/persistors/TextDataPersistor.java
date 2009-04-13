package org.encog.persist.persistors;

import org.encog.neural.data.TextData;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

public class TextDataPersistor implements Persistor {

	public EncogPersistedObject load(ReadXML in) {
		String name = in.getTag().getAttributeValue("name");
		String description = in.getTag().getAttributeValue("description");
		TextData result = new TextData();
		in.readToTag();		
		String text = in.getTag().getName();
		result.setName(name);
		result.setDescription(description);
		result.setText(text);
		return result;
	}

	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_TEXT, out, obj, true);
		TextData text = (TextData)obj;
		out.addCDATA(text.getText());
		out.endTag();
	}

}
