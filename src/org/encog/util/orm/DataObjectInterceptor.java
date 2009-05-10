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

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple Hibernate interceptor that ensures that the last update field on
 * Encog database persisted objects are updated.
 * 
 * @author jheaton
 * 
 */
public class DataObjectInterceptor extends EmptyInterceptor {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -6610167494842830683L;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Called when a object is flushed.  Used to set the lastUpdate field to
	 * the current date.
	 * @param entity The object currently being processed.
	 * @param id The ID.
	 * @param currentState The current value of the fields.
	 * @param previousState The previous values of the fields.
	 * @param propertyNames The names of the fields.
	 * @param types The field types.
	 * @return True if a value changes.
	 */
	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id,
			final Object[] currentState, final Object[] previousState,
			final String[] propertyNames, final Type[] types) {

		if (entity instanceof DataObject) {

			for (int i = 0; i < propertyNames.length; i++) {
				if ("lastUpdate".equals(propertyNames[i])) {
					currentState[i] = new Date();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Called when an object is saved.  Used to update the create field on 
	 * initial object creation.
	 * @param entity The object being saved.
	 * @param id The id of the object being saved.
	 * @param state The state of this object.
	 * @param propertyNames The names of the fields.
	 * @param types The field types.
	 * @return True if a change was made.
	 */
	@Override
	public boolean onSave(final Object entity, final Serializable id,
			final Object[] state, final String[] propertyNames,
			final Type[] types) {

		if (entity instanceof DataObject) {

			for (int i = 0; i < propertyNames.length; i++) {
				if ("created".equals(propertyNames[i])) {
					state[i] = new Date();
					return true;
				}
			}
		}
		return false;
	}

}
