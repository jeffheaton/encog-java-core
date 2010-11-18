/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.persist;

/**
 * The idea of the Encog persisted collection is that the entire file might be
 * quite long and should not be read into memory in its entirity. Directory
 * entry classes allow you to list the contents of a file without loading the
 * entire file.
 *
 * @author jheaton
 *
 */
public class DirectoryEntry implements Comparable<DirectoryEntry> {

	/**
	 * The type of object that this is.
	 */
	private final String type;

	/**
	 * The name of this object.
	 */
	private final String name;

	/**
	 * The description for this object.
	 */
	private final String description;

	/**
	 * Construct a directory entry for the specified object.
	 *
	 * @param obj
	 *            The Encog object.
	 */
	public DirectoryEntry(final EncogPersistedObject obj) {

		String type = obj.getClass().getSimpleName();

		if (type.equalsIgnoreCase("BasicNeuralDataSet")) {
			type = EncogPersistedCollection.TYPE_TRAINING;
		}

		this.type = type;
		this.description = obj.getDescription();
		this.name = obj.getName();
	}

	/**
	 * Construct a directory entry from strings.
	 *
	 * @param type
	 *            The type of object.
	 * @param name
	 *            The name of this object.
	 * @param description
	 *            The description for this object.
	 */
	public DirectoryEntry(final String type, final String name,
			final String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(final DirectoryEntry other) {
		if (other.getType().equals(other.getType())) {
			final String c = getName() == null ? "" : getName();
			return c.compareTo(other.getName());
		} else {
			return getType().compareTo(other.getType());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof DirectoryEntry)) {
			return false;
		} else {
			return compareTo((DirectoryEntry) other) == 0;
		}
	}

	/**
	 * @return The description for this object.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The name of this object.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The type of this object.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return A simple hash code for this object.
	 */
	@Override
	public int hashCode() {
		return this.name.hashCode() + this.type.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[DirectoryEntry:type=");
		result.append(getType());
		result.append(",name=");
		result.append(getName());
		result.append("]");
		return result.toString();
	}

}
