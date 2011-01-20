package org.encog.persist.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.innovation.Innovation;
import org.encog.ml.genetic.innovation.InnovationList;
import org.encog.ml.genetic.population.Population;
import org.encog.parse.ParseError;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.util.obj.ReflectionUtil;

public class PersistedObject extends PersistedProperty {

	private final Map<String, PersistedProperty> data = new HashMap<String, PersistedProperty>();
	private String objectType;

	public PersistedObject() {
		super(false);
	}

	/**
	 * @return the data
	 */
	public Map<String, PersistedProperty> getData() {
		return data;
	}

	public void clear(String objectType) {
		this.objectType = objectType;
		this.data.clear();
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Set a property as a string value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, String value, boolean attribute) {
		if (value != null) {
			this.data.put(name, new PersistedValue(value, attribute));
		}
	}

	/**
	 * Set a property as a double value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, int value, boolean attribute) {
		this.data.put(name, new PersistedValue(value, attribute));
	}

	/**
	 * Set a property as a double value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, double value, boolean attribute) {
		this.data.put(name, new PersistedValue(value, attribute));
	}

	/**
	 * Set a property as a double array value.
	 * @param name The name of the property.
	 * @param value The value to set to.
	 */
	public void setProperty(String name, double[] value) {
		if (value != null) {
			this.data.put(name, new PersistedDoubleArray(value));
		}
	}

	public void setStandardProperties(EncogPersistedObject obj) {
		setProperty(PersistConst.NAME, obj.getName(), true);
		setProperty(PersistConst.DESCRIPTION, obj.getDescription(), true);
		setProperty(PersistConst.NATIVE, obj.getClass().getName(), true);

	}

	public void requireType(String t) {
		if (!t.equals(this.objectType)) {
			throw new PersistError("Expected object type: " + t
					+ ", but found: " + this.objectType);
		}

	}

	private boolean require(String name, boolean required) {
		if (!this.data.containsKey(name)) {
			if (required)
				throw new PersistError("The property " + name
						+ " was required.");
			return true;
		}
		return false;
	}

	public String getPropertyString(String name, boolean required) {
		if (require(name, required)) {
			return null;
		}
		PersistedProperty result = this.data.get(name);
		return result.getString();
	}

	public double[] getPropertyDoubleArray(String name, boolean required) {
		try {
			if (require(name, required)) {
				return null;
			}

			PersistedProperty result = this.data.get(name);
			if (result instanceof PersistedDoubleArray) {
				PersistedDoubleArray a = (PersistedDoubleArray) result;
				return a.getDoubleArray();
			}
			throw new PersistError("Expected double array for " + name);
		} catch (Exception e) {
			throw new ParseError("Invalid double array: " + name);
		}
	}

	@Override
	public String getString() {
		return this.objectType;
	}

	public int getPropertyInt(String name, boolean required) {
		String str = this.getPropertyString(name, required);
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			throw new PersistError("Property: " + name
					+ ", had invalid integer:" + str);
		}
	}

	public double getPropertyDouble(String name, boolean required) {
		String str = this.getPropertyString(name, required);
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException ex) {
			throw new PersistError("Property: " + name
					+ ", had invalid double:" + str);
		}
	}

	public void setProperty(String name, Matrix value) {
		if (value != null) {
			this.data.put(name, new PersistedMatrix(value));
		}
	}

	public Matrix getPropertyMatrix(String name, boolean required) {
		try {
			if (require(name, required)) {
				return null;
			}

			PersistedProperty result = this.data.get(name);
			if (result instanceof PersistedMatrix) {
				PersistedMatrix m = (PersistedMatrix) result;
				return m.getMatrix();
			}
			throw new PersistError("Expected double array for " + name);
		} catch (Exception e) {
			throw new ParseError("Invalid double array: " + name);
		}
	}

	public void setProperty(String name, EncogPersistedObject obj) {
		if (obj != null) {
			if (!obj.supportsMapPersistence()) {
				throw new PersistError("Can't persist "
						+ obj.getClass().getName()
						+ " it does not support persistence.");
			}

			PersistedObject po = new PersistedObject();
			obj.persistToMap(po);
			this.data.put(name, po);
		}
	}

	public void setProperty(String name, ActivationFunction activation) {
		if (activation != null) {
			this.data.put(name, new PersistedActivationFunction(activation));
		}
	}

	public ActivationFunction getPropertyActivationFunction(String name,
			boolean required) {

		if (require(name, required)) {
			return null;
		}

		PersistedProperty result = this.data.get(name);
		if (result instanceof PersistedActivationFunction) {
			PersistedActivationFunction a = (PersistedActivationFunction) result;
			return a.getActivationFunction();
		}
		throw new PersistError("Expected activation function for " + name);

	}

	public void setProperty(String name, List<PersistedObject> list) {
		if (list != null) {
			this.data.put(name, new PersistedValueArray(list));
		}		
	}

	public void setProperty(String name, boolean value,
			boolean attribute) {
		this.setProperty(name, value?"t":"f",attribute);
		
	}

	public boolean getPropertyBoolean(String name, boolean required) {
		String str = this.getPropertyString(name, required);
		if( str.length()>0 && str.trim().toLowerCase().charAt(0)=='t')
			return true;
		return false;
	}

	public List<PersistedObject> getPropertyValueArray(String name) {
		
		if (require(name, true)) {
			return null;
		}

		PersistedProperty result = this.data.get(name);
		if (result instanceof PersistedValueArray) {
			PersistedValueArray pva = (PersistedValueArray) result;
			return pva.getList();
		}
		throw new PersistError("Expected object array for " + name);

	}

	public void setPropertyGenericList(String name,
			List<?> list) {
		
		List<PersistedObject> temp = new ArrayList<PersistedObject>();
		
		for(Object obj: list )
		{
			if( obj instanceof EncogPersistedObject)
			{
				PersistedObject ep = new PersistedObject();
				EncogPersistedObject epo = (EncogPersistedObject)obj;
				epo.persistToMap(ep);
				temp.add(ep);
			}
			else
			{
				throw new PersistError("Do not know how to persist " + obj.getClass().getName());
			}
		}
		
		setProperty( name, temp );
		
	}
	
	public void getPropertyGenericList(String name,
			List list) {
		
		List<PersistedObject> temp = this.getPropertyValueArray(name);
		
		for(PersistedObject obj: temp )
		{
			String type = obj.getObjectType();
			Class<?> clazz = ReflectionUtil.resolveEncogClass(type);
			
			if( clazz == null ) {
				throw new PersistError("Unregistered class: " + type);
			}
			
			EncogPersistedObject epo;
			try {
				epo = (EncogPersistedObject)clazz.newInstance();
			} catch (InstantiationException e) {
				throw new PersistError(e);
			} catch (IllegalAccessException e) {
				throw new PersistError(e);
			}
			epo.persistFromMap(obj);
			list.add(epo);
		}
		
		setProperty( name, temp );
		
	}

	public void setProperty(String name, int[] value) {
		if (value != null) {
			this.data.put(name, new PersistedIntArray(value));
		}		
	}


}
