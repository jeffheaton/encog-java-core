/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */

package org.encog.persist.persistors;

import org.encog.neural.data.TextData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.script.EncogScript;

/**
 * The Encog persistor used to persist the TextData class.
 * 
 * @author jheaton
 */
public class EncogScriptPersistor implements Persistor {

	public static final String TAG_SOURCE = "source";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	
	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		final String name = in.getTag().getAttributeValue("name");
		final String description = in.getTag().getAttributeValue("description");
		final EncogScript result = new EncogScript();
		
		result.setName(name);
		result.setDescription(description);
		
		
		final String end = in.getTag().getName();
		while (in.readToTag()) {
			if (in.is(EncogScriptPersistor.TAG_SOURCE, true)) {
				handleSource(in,result);
			}
			else if (in.is(end, false)) {
				break;
			}
		}

		return result;
	}
	
	private void handleSource(final ReadXML in, EncogScript script)
	{
		String language = in.getTag().getAttributeValue(ATTRIBUTE_LANGUAGE);
		script.setLanguage(language);
		in.readToTag();		
		final String text = in.getTag().getName();
		script.setSource(text);

	}

	/**
	 * Save the specified Encog object to an XML writer.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer to save to.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_SCRIPT, out,
				obj, true);
		final EncogScript text = (EncogScript) obj;
		out.addAttribute(ATTRIBUTE_LANGUAGE, text.getLanguage());
		out.beginTag(TAG_SOURCE);
		out.addCDATA(text.getSource());
		out.endTag();
		out.endTag();
	}

}
