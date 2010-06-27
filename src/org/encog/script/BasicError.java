package org.encog.script;

import org.encog.EncogError;

public class BasicError extends EncogError {
	
	public BasicError(ErrorNumbers id)
	{
		super("");
		this.id = id;
	}
	
	private ErrorNumbers id;
	private String description;

}
