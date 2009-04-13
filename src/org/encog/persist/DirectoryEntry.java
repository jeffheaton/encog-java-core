package org.encog.persist;

import org.encog.neural.activation.ActivationBiPolar;

public class DirectoryEntry {
	
	private final String type;
	private final String name;
	private final String description;
	
	public DirectoryEntry(String type, String name,
			String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	public DirectoryEntry(EncogPersistedObject obj) {
		this(obj.getClass().getSimpleName(),obj.getName(),obj.getDescription());
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	
}
