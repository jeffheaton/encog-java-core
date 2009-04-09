package org.encog.parse.tags.read;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.encog.parse.ParseError;
import org.encog.parse.tags.Tag.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadXML extends ReadTags {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ReadXML(InputStream is) {
		super(is);
	}
	
	private boolean shouldContinue(int ch)
	{
		if( ch==-1 || ch==0 )
			return false;
		else
			return true;
	}

	public String readTextToTag() {
		int ch;
		StringBuilder result = new StringBuilder();
		while( shouldContinue(ch=this.read()) )
		{
			result.append((char)ch);
		}
		return result.toString();
	}

	public Map<String, String> readPropertyBlock() {
		Map<String, String> result = new HashMap<String, String>();
		
		String endingBlock = this.getTag().getName();
		
		while( this.readToTag() )
		{
			if( this.getTag().getName().equals(endingBlock) &&
				this.getTag().getType() == Type.END )
			{
				break;
			}
			String name = this.getTag().getName();
			String value = this.readTextToTag().trim();
			result.put(name, value);
		}
		
		return result;
	}

	public int readIntToTag() {
		try
		{
			String str = this.readTextToTag();
			return Integer.parseInt(str);
		}
		catch(NumberFormatException e)
		{
			if(logger.isErrorEnabled())
			{
				logger.error("Exception",e);
			}
			throw new ParseError(e);
		}
	}

}
