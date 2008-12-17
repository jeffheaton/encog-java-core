package org.encog.neural.persist.persistors.generic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.EncogError;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Object2XML {
	
	public void save(EncogPersistedObject object, TransformerHandler hd) {

		try {
			final AttributesImpl atts = EncogPersistedCollection
					.createAttributes(object);
			hd.startElement("", "", object.getClass().getSimpleName(), atts);
			atts.clear();

			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);

				if (field.getName().equalsIgnoreCase("name")
						|| field.getName().equalsIgnoreCase("description"))
					continue;

				Class<?> type = field.getType();

				if ((field.getModifiers() & Modifier.FINAL) == 0) {
					Object value = field.get(object);
					if (value != null) {
						if (value instanceof Collection) {
							hd.startElement("", "", field.getName(), atts);
							saveCollection(hd, (Collection) value);
							hd.endElement("", "", field.getName());
						}
						else
							EncogPersistedCollection.addProperty(hd, field
								.getName(), value.toString());
					}
				}
			}

			hd.endElement("", "", object.getClass().getSimpleName());

		} catch (SAXException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

	private void saveCollection(TransformerHandler hd, Collection<?> value) {

			for (Object obj : value) {
				if (obj instanceof String) {
					EncogPersistedCollection.addProperty(hd, "S", obj.toString());
				}
			}
	}
}
