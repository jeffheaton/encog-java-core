package org.encog.script.basic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.encog.EncogError;
import org.encog.script.EncogScript;
import org.encog.util.ReflectionUtil;

public class BasicModule extends BasicObject {
	
	
	public BasicModule(BasicProgram program)
	{
		this.program = program;
	}

	public void Clear()
	{
		programLines.clear();
		programLabels.clear();
	}
	
	public void Load(EncogScript script) {
		try {
			this.addto = this.programLines;
			
			final InputStream is = new ByteArrayInputStream(script.getSource()
					.getBytes());
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;

			while ((line = reader.readLine()) != null) {
				String check = line.trim();
				
				if( check.length()==0 )
					continue;
				
				if( check.charAt(0)=='\'')
					continue;
				
				AddLine(line);
			}
		} catch (IOException e) {
			throw new EncogError(e);
		}
	}
	
	public BasicLine Go(String label)
	{
		if( !this.programLabels.containsKey(label.toUpperCase()) )
			throw(new BasicError(ErrorNumbers.errorLabel));

		int index = this.programLabels.get(label);
		return this.programLines.get(index);
	}
	
	public void AddLine(String line)
	{
		boolean createSub = false;
		int ptr;
		int ptr2;
		String label = "";
		BasicLine bl;

			line = BasicUtil.basicToUpper(line);

			// Look for a label

			ptr=line.indexOf(':');
			
			if(ptr!=-1)
			{
				// Check to see if this ':' is really that of a label
				ptr2=0;
				while((Character.isLetterOrDigit(ptr)) && (line.charAt(ptr2)!=32) )
					ptr2++;

				// If it is a label, clip it and mark it as a label
				if(ptr==ptr2)
				{
					label = line.substring(0,ptr);
					line = line.substring(ptr+1);
				}
			}

			// Not a label, so check for a FUNCTION or SUB

			if(label.length()==0)
			{
				int l = 0;
				
				while( ( (line.charAt(l)==' ') || (line.charAt(l)=='\t') ) && l<line.length() )
					l++;
				
				ptr=BasicUtil.FindKeyword(line,"SUB");
				if(ptr==-1)
					ptr=BasicUtil.FindKeyword(line,"FUNCTION");

				if(ptr!=-1)
				{
					ptr = line.indexOf(' ',ptr);
					
					if(ptr!=-1)
					{
						while( " \t".indexOf(line.charAt(ptr))!=-1 )
							ptr++;
						
						StringBuilder b = new StringBuilder();
						b.append('~');
						
						while( ptr<line.length() && " \t(".indexOf(line.charAt(ptr))==-1  )
							b.append(line.charAt(ptr++));
						label = b.toString();
					}
					
					if( BasicUtil.FindKeyword(label.substring(1))!=null)
						throw(new BasicError(ErrorNumbers.errorIllegalFunctionName));

					if(this.programLabels.containsKey(label))
						throw(new BasicError(ErrorNumbers.errorAlreadyDeclared));
					
					createSub = true;
				}
			}

			if(label.length()>0)
			{
				if(BasicUtil.FindKeyword(label)!=null)
					throw( new BasicError(ErrorNumbers.errorIllegalFunctionName));
			}

			int number = this.addto.size();
			this.addto.add(bl=new BasicLine(line));
			bl.setNumber(number);
			if( label.length()>0 )
			{
				bl.setLabel(label);
				this.programLabels.put(label,number);
			}	
			
			if( createSub )
				this.addto = bl.getSub();
	}
	
	public BasicLine FindFunction(String label)
	{
		String key = "~"+label.toUpperCase();
		
		if( !this.programLabels.containsKey(key))
			return null;
			
		int index = this.programLabels.get(key);
		return this.programLines.get(index);
	}	
	
	public List<BasicLine> getProgramLines() {
		return programLines;
	}
	
	public Map<String,Integer> getProgramLabels()
	{
		return this.programLabels;
	}
	
	public BasicProgram getProgram() {
		return program;
	}

	private Map<String,Integer> programLabels = new HashMap<String,Integer>();
	private List<BasicLine> programLines = new ArrayList<BasicLine>();
	private List<BasicLine> addto;
	private BasicProgram program;
}
