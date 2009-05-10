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
package org.encog.util.orm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the superclass for all Encog Hibernate persisted classes. It provides
 * basic versioning and object id functions required by all Encog database
 * persisted classes.
 * 
 * Do not confuse this class with the EncogPersistedCollection class, which is
 * used to save neural networks and related files to disk. This class is
 * exclusively used for database mapped objects.
 * 
 * @author jheaton
 * 
 */
@MappedSuperclass
public abstract class DataObject implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7032283205435194035L;

	/**
	 * Convert a Boolean value.
	 * @param value The boolean value.
	 * @return The boolean value.
	 */
	protected static boolean getBooleanValue(final Boolean value) {
		return Boolean.valueOf(String.valueOf(value));
	}

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	@Transient
	private final transient Logger logger = LoggerFactory.getLogger(this
			.getClass());

	/**
	 * The unique identifier for this object.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id = null;

	/**
	 * The version of this object.
	 */
	@Version
	@Column(name = "version")
	private int version = 0;

	/**
	 * When this object was last updated.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update")
	private Date lastUpdate = new Date();

	/**
	 * When was this object created.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();

	/**
	 * Copy this object from another.
	 * @param source The object being copied.
	 */
	protected void copy(final DataObject source) {
		this.id = source.id;
		this.version = source.version;
		this.lastUpdate = source.lastUpdate;
	}

	/**
	 * Determine if this object equals another.
	 * @param obj The other object to test equality with.
	 * @return True if the two objects are equal.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataObject)) {
			return false;
		}
		final DataObject other = (DataObject) obj;
		if ((this.id != null) && (other.id != null)) {
			if (this.id != other.id) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return When this object was created.
	 */
	public Date getCreated() {
		return this.created;
	}

	/**
	 * @return The id for this object.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @return When this object was last updated.
	 */
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * @return The version of this object.
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * Set the create date for this object.
	 * @param created The date this object was created.
	 */
	public void setCreated(final Date created) {
		this.created = created;
	}

	/**
	 * Set the unique identifier for this object.
	 * @param id The unique identifier for this object.
	 */
	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Set the last update date.
	 * @param lastUpdate The last update date.
	 */
	public void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Set the version of this object.
	 * @param version The version.
	 */
	@SuppressWarnings("unused")
	private void setVersion(final int version) {
		this.version = version;
	}

	/**
	 * Validate this object.
	 */
	public abstract void validate();

}