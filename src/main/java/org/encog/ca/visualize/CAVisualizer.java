package org.encog.ca.visualize;

import java.awt.Image;

public interface CAVisualizer {

	int getZoom();
	void setZoom(int z);
	Image visualize();

}
