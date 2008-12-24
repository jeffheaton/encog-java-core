package org.encog.util.orm;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class DataObjectInterceptor extends EmptyInterceptor {

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {

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

	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {

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

}
