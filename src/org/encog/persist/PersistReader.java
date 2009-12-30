/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.persist;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.persistors.PersistorUtil;
import org.encog.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for reading Encog persited object files.
 * 
 * @author jheaton
 * 
 */
public class PersistReader {

	/**
	 * The name attribute.
	 */
	public static final String ATTRIBUTE_NAME = "name";

	/**
	 * The objects tag.
	 */
	public static final String TAG_OBJECTS = "Objects";

	/**
	 * The XML reader.
	 */
	private final ReadXML in;

	/**
	 * The input stream.
	 */
	private final InputStream fileInput;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a persist reader.
	 * 
	 * @param location
	 *            The location to use.
	 */
	public PersistReader(final PersistenceLocation location) {
		this.fileInput = location.createInputStream();
		this.in = new ReadXML(this.fileInput);
	}

	/**
	 * Advance to the specified object.
	 * 
	 * @param name
	 *            The name of the object looking for.
	 * @return The beginning element of the object found.
	 */
	public boolean advance(final String name) {
		advanceObjectsCollection();
		return advanceObjects(name);
	}

	/**
	 * Once you are in the objects collection, advance to a specific object.
	 * 
	 * @param name
	 *            The name of the object to advance to.
	 * @return The beginning tag of that object if its found, null otherwise.
	 */
	private boolean advanceObjects(final String name) {

		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {
				final String elementName = this.in.getTag().getAttributeValue(
						"name");

				if ((elementName != null) && elementName.equals(name)) {
					return true;
				} else {
					skipObject();
				}
			}
		}
		return false;
	}

	/**
	 * Advance to the objects collection.
	 */
	public void advanceObjectsCollection() {
		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();
			if ((type == Type.BEGIN)
					&& this.in.getTag().getName().equals(
							PersistReader.TAG_OBJECTS)) {
				return;
			}
		}

		final String str = "Can't find objects collection, invalid file.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);

	}

	/**
	 * Advance to the specified tag.
	 * 
	 * @param tag
	 *            The tag to search for.
	 * @return True if the tag was found.
	 */
	private boolean advanceToTag(final String tag) {
		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {

				if (this.in.getTag().getName().equals(tag)) {
					return true;
				} else {
					skipObject();
				}
			}
		}
		return false;
	}

	/**
	 * Build a directory entry list for the file.
	 * 
	 * @return A list of objects in the file.
	 */
	public Set<DirectoryEntry> buildDirectory() {
		final Set<DirectoryEntry> result = new TreeSet<DirectoryEntry>();
		advanceObjectsCollection();

		while (this.in.readToTag()) {
			if (this.in.is(PersistReader.TAG_OBJECTS, false)) {
				break;
			}

			final String type = this.in.getTag().getName();
			final String name = this.in.getTag().getAttributeValue("name");
			final String description = this.in.getTag().getAttributeValue(
					"description");

			final DirectoryEntry entry = new DirectoryEntry(type, name,
					description);
			result.add(entry);

			skipObject();
		}

		return result;
	}

	/**
	 * Close the file.
	 */
	public void close() {
		try {
			this.fileInput.close();
		} catch (final IOException e) {
			throw new PersistError(e);
		}

	}

	/**
	 * Copy all of the attributes to the writer.
	 * 
	 * @param out
	 *            The XML writer.
	 * @param replace
	 *            A map of attributes to replace. This allows new values to be
	 *            specified for select attributes.
	 */
	private void copyAttributes(final WriteXML out,
			final Map<String, String> replace) {
		for (final String key : this.in.getTag().getAttributes().keySet()) {
			String value = this.in.getTag().getAttributeValue(key);
			if ((replace != null) && replace.containsKey(key)) {
				value = replace.get(key);
			}
			out.addAttribute(key, value);
		}
	}

	/**
	 * Copy an XML object, no need to know what it contains, just copy it. This
	 * way we will not damage unknown objects during a merge.
	 * 
	 * @param out
	 *            The XML writer.
	 * @param replace
	 *            A map of attributes to replace. This allows new values to be
	 *            specified for select attributes.
	 */
	private void copyXML(final WriteXML out, 
			final Map<String, String> replace) {
		final StringBuilder text = new StringBuilder();
		int depth = 0;
		int ch;
		copyAttributes(out, replace);
		final String contain = this.in.getTag().getName();

		out.beginTag(contain);

		while ((ch = this.in.read()) != -1) {
			final Type type = this.in.getTag().getType();

			if (ch == 0) {
				if (type == Type.BEGIN) {
					if (text.length() > 0) {
						out.addText(text.toString());
						text.setLength(0);
					}

					copyAttributes(out, null);
					out.beginTag(this.in.getTag().getName());
					depth++;
				} else if (type == Type.END) {
					if (text.length() > 0) {
						out.addText(text.toString());
						text.setLength(0);
					}

					if (depth == 0) {
						break;
					} else {
						out.endTag(this.in.getTag().getName());
					}

					depth--;
				}
			} else {
				text.append((char) ch);
			}

		}

		out.endTag(contain);
	}

	/**
	 * @return The ReadXML object being used by this object.
	 */
	public ReadXML getXMLInput() {
		return this.in;
	}

	/**
	 * Obtain the Encog header from the file.
	 * 
	 * @return Name value pair map of the header attributes.
	 */
	public Map<String, String> readHeader() {
		Map<String, String> headers = null;
		if (advanceToTag("Document")) {
			if (advanceToTag("Header")) {
				headers = this.in.readPropertyBlock();
			}
		}
		return headers;
	}

	/**
	 * Read until the next tag of the specified name.
	 * 
	 * @param name
	 *            The name searched for.
	 * @return True if the tag was found.
	 */
	public boolean readNextTag(final String name) {

		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {
				if (this.in.getTag().getName().equals(name)) {
					return true;
				} else {
					skipObject();
				}
			}
		}
		return false;
	}

	/**
	 * Read all text until the specified ending tag is found.
	 * 
	 * @param name
	 *            The tag.
	 * @return The text found.
	 */
	public String readNextText(final String name) {
		final StringBuilder result = new StringBuilder();
		return result.toString();
	}

	/**
	 * Read the specific object, search through the objects until its found.
	 * 
	 * @param name
	 *            The name of the object you are looking for.
	 * @return The object found, null if not found.
	 */
	public EncogPersistedObject readObject(final String name) {

		// did we find the object?
		if (advance(name)) {
			final String objectType = this.in.getTag().getName();
			Persistor persistor = PersistorUtil.createPersistor(objectType);
			if (persistor == null) {
				final Class< ? > clazz = ReflectionUtil
						.resolveEncogClass(objectType);
				EncogPersistedObject temp;
				try {
					temp = (EncogPersistedObject) clazz.newInstance();
				} catch (final InstantiationException e) {
					throw new PersistError(e);
				} catch (final IllegalAccessException e) {
					throw new PersistError(e);
				}
				persistor = temp.createPersistor();
			}
			if (persistor == null) {
				throw new PersistError("Do not know how to load: " 
						+ objectType);
			}
			return persistor.load(this.in);
		} else {
			return null;
		}
	}

	/**
	 * Read the value in a period delimited string. For example
	 * property.name.value.
	 * 
	 * @param name
	 *            The property to read.
	 * @return The value found at the specified property.
	 */
	public String readValue(final String name) {

		final StringTokenizer tok = new StringTokenizer(name, ".");
		while (tok.hasMoreTokens()) {
			final String subName = tok.nextToken();
			if (!readNextTag(subName)) {
				return null;
			}
		}

		return readNextText(this.in.getTag().getName());
	}

	/**
	 * Modify the properties of this object.
	 * 
	 * @param out
	 *            The XML writer.
	 * @param targetName
	 *            The name of the object to change.
	 * @param newName
	 *            The new name of this object.
	 * @param newDesc
	 *            The new description of this object.
	 */
	public void saveModified(final WriteXML out, final String targetName,
			final String newName, final String newDesc) {
		advanceObjectsCollection();

		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {
				final String name = this.in.getTag().getAttributeValue(
						PersistReader.ATTRIBUTE_NAME);
				if (name.equals(targetName)) {
					final Map<String, String> replace = 
						new HashMap<String, String>();
					replace.put("name", newName);
					replace.put("description", newDesc);
					copyXML(out, replace);
				} else {
					copyXML(out, null);
				}
			}
		}
	}

	/**
	 * Save all objects to the specified steam, skip the one specified by the
	 * skip parameter. Do not attempt to understand the structure, just copy.
	 * 
	 * @param out
	 *            The XML writer to save the objects to.
	 * @param skip
	 *            The object to skip.
	 */
	public void saveTo(final WriteXML out, final String skip) {
		advanceObjectsCollection();

		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();
			if (type == Type.BEGIN) {
				final String name = this.in.getTag().getAttributeValue(
						PersistReader.ATTRIBUTE_NAME);
				if (name.equals(skip)) {
					skipObject();
				} else {
					copyXML(out, null);
				}
			}
		}
	}

	/**
	 * Skip the current specified object.
	 */
	private void skipObject() {
		int depth = 0;
		while (this.in.readToTag()) {
			final Type type = this.in.getTag().getType();

			switch (type) {
			case END:
				if (depth == 0) {
					return;
				}
				depth--;
				break;
			case BEGIN:
				depth++;
				break;
			}
		}
	}

}
