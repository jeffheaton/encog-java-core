/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
	 * 
	 * @param other
	 *            The other object.
	 * @return 0 if equal.
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
	 * Returns true if the two objects are equal.
	 * 
	 * @param other
	 *            The other object.
	 * @return True if equal.
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
	 * @return This object as a string.
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
