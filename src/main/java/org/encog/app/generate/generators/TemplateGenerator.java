package org.encog.app.generate.generators;

import org.encog.app.analyst.EncogAnalyst;

public interface TemplateGenerator extends LanguageSpecificGenerator {

	void generate(EncogAnalyst analyst);

}
