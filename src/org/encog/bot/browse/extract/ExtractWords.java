package org.encog.bot.browse.extract;

import java.util.Collection;

import org.encog.bot.browse.WebPage;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.encog.parse.Parse;
import org.encog.parse.signal.Signal;

public class ExtractWords extends BasicExtract {
	
	Parse parse;
	
	public ExtractWords()
	{
		parse = new Parse();
		parse.load();
	}
	
	public void extract(WebPage page)
	{

		for(DataUnit unit : page.getData() )
		{				
			if( unit instanceof TextDataUnit )
			{
				TextDataUnit text = (TextDataUnit)unit;
				Signal signal = parse.parse(text.getText());
				Collection<Signal> list = signal.findByType("word");
				for(Signal word: list)
				{
					distribute(word.toString());
				}
			}
		}
	}
}
