package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class MatrixPersistor implements Persistor {

	public EncogPersistedObject load(Element matrixElement)
			throws NeuralNetworkException {
		int rows = Integer.parseInt(matrixElement.getAttribute("rows"));
        int cols = Integer.parseInt(matrixElement.getAttribute("cols"));
        Matrix result = new Matrix(rows, cols);

        int row = 0;
       
        for (Node child = matrixElement.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            if (!(child instanceof Element))
                continue;
            Element node = (Element) child;
            if (node.getNodeName().equals("row")) {
                for(int col=0;col<cols;col++)
                {
                    double value = Double.parseDouble(node.getAttribute("col"+col));
                    result.set(row, col, value);
                }
                row++;
            }
        }

        return result;
	}

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkException {
		try {
			Matrix matrix = (Matrix) object;
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "", "rows", "CDATA", "" + matrix.getRows());
			atts.addAttribute("", "", "cols", "CDATA", "" + matrix.getCols());
			hd.startElement("", "", "Matrix", atts);
			for (int row = 0; row < matrix.getRows(); row++) {
				atts.clear();
				for (int col = 0; col < matrix.getCols(); col++) {
					atts.addAttribute("", "", "col" + col, "CDATA", ""
							+ matrix.get(row, col));
				}

				hd.startElement("", "", "row", atts);
				hd.endElement("", "", "row");
			}
			hd.endElement("", "", "Matrix");
		} catch (SAXException e) {
			throw new NeuralNetworkException(e);
		}
	}

}
