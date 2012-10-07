package org.encog.app.generate.generators;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.generate.program.EncogProgram;

public interface ProgramGenerator extends LanguageSpecificGenerator {
	void generate(EncogProgram program, boolean embed);
}
