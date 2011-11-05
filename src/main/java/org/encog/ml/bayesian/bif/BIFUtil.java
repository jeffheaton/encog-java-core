package org.encog.ml.bayesian.bif;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianNetwork;
import org.xml.sax.SAXException;

public class BIFUtil {

	public static BayesianNetwork readBIF(String f) {
		return readBIF(new File(f));
	}

	public static BayesianNetwork readBIF(File f) {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(f);
			return readBIF(fis);
		} catch (IOException ex) {
			throw new BayesianError(ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
					// who cares at this point.
				}
			}
		}
	}

	public static BayesianNetwork readBIF(InputStream is) {
		try {
			BIFHandler h = new BIFHandler();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(is, h);
			return h.getNetwork();
		} catch (IOException ex) {
			throw new BayesianError(ex);
		} catch (ParserConfigurationException ex) {
			throw new BayesianError(ex);
		} catch (SAXException ex) {
			throw new BayesianError(ex);
		}
	}

}
