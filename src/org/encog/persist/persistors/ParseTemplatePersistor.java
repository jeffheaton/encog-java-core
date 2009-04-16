/*
 * Encog Artificial Intelligence Framework v2.x
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
package org.encog.persist.persistors;

import org.encog.parse.ParseTemplate;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.Object2XML;
import org.encog.persist.persistors.generic.XML2Object;

/**
 * The Encog persistor used to persist the ParseTemplate class.
 * 
 * @author jheaton
 */
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
