/*
 * Encog Artificial Intelligence Framework v1.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

		//try {
			/*final AttributesImpl atts = EncogPersistedCollection
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
		}*/

	}

	private void saveCollection(TransformerHandler hd, Collection<?> value) {
/*
			for (Object obj : value) {
				if (obj instanceof String) {
					EncogPersistedCollection.addProperty(hd, "S", obj.toString());
				}
			}*/
	}
}
