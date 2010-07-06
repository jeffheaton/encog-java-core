package org.encog.script;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.EncogScriptPersistor;
import org.encog.script.basic.Basic;
import org.encog.script.basic.BasicModule;
import org.encog.script.basic.BasicParse;
import org.encog.script.basic.BasicProgram;
import org.encog.script.basic.BasicVariable;
import org.encog.script.basic.Err;
import org.encog.script.basic.console.ConsoleInputOutput;
import org.encog.script.basic.console.NullConsole;

public class EncogScript implements EncogPersistedObject {

	private String name;
	private String description;
	private String source;
	private BasicProgram program;
	private ConsoleInputOutput console = new NullConsole();
	
	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;

	
	@Override
	public Persistor createPersistor() {
		return new EncogScriptPersistor();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}
	
	public void load()
	{
		this.program = new BasicProgram();
		this.program.LoadModule(this);
	}
	
	
	
	/**
	 * @return the console
	 */
	public ConsoleInputOutput getConsole() {
		return console;
	}

	/**
	 * @param console the console to set
	 */
	public void setConsole(ConsoleInputOutput console) {
		this.console = console;
	}

	public BasicVariable call(String name)
	{
		BasicVariable result = new BasicVariable();
		this.program.setConsole(this.console);
		
		if(!this.program.Call(name, result))
		{
			throw new EncogError("Can't find main sub in script.");
		}
		
		return result;
	}
	
	public BasicVariable call()
	{
		return call("MAIN");
	}
}
