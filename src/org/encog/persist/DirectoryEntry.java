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
public class DirectoryEntry implements Comparable {

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
		this(obj.getClass().getSimpleName(), obj.getName(), obj
				.getDescription());
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
	 * Compare the two objects.
	 * @param other The other object.
	 * @return 0 if equal.
	 */
	public int compareTo(final Object other) {
		if (other instanceof DirectoryEntry) {
			final DirectoryEntry other2 = (DirectoryEntry) other;
			if (other2.getType().equals(other2.getType())) {
				String c = getName()==null?"":getName();
				return c.compareTo(other2.getName());
			} else {
				return compareTo(other2.getType());
			}
		} else {
			return 1;
		}
	}

	/**
	 * Returns true if the two objects are equal.
	 * @param other The other object.
	 * @return True if equal.
	 */
	@Override
	public boolean equals(final Object other) {
		return compareTo(other) == 0;
	}
	
	/**
	 * @return A simple hash code for this object.
	 */
	public int hashCode() {
		return this.name.hashCode() + this.type.hashCode();
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
	 * @return This object as a string.
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[DirectoryEntry:type=");
		result.append(this.getType());
		result.append(",name=");
		result.append(this.getName());
		result.append("]");
		return result.toString();
	}

}
