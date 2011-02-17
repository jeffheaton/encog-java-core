package org.encog.app.analyst.script.information;

import org.encog.util.csv.CSVFormat;

public class AnalystInformation {
	
	private String dataSource = "";
	private CSVFormat dataSourceFormat = CSVFormat.DECIMAL_POINT;
	private boolean dataSourceHeaders = false;
	/**
	 * @return the dataSource
	 */
	public String getDataSource() {
		return dataSource;
	}
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * @return the dataSourceFormat
	 */
	public CSVFormat getDataSourceFormat() {
		return dataSourceFormat;
	}
	/**
	 * @param dataSourceFormat the dataSourceFormat to set
	 */
	public void setDataSourceFormat(CSVFormat dataSourceFormat) {
		this.dataSourceFormat = dataSourceFormat;
	}
	/**
	 * @return the dataSourceHeaders
	 */
	public boolean isDataSourceHeaders() {
		return dataSourceHeaders;
	}
	/**
	 * @param dataSourceHeaders the dataSourceHeaders to set
	 */
	public void setDataSourceHeaders(boolean dataSourceHeaders) {
		this.dataSourceHeaders = dataSourceHeaders;
	}
	public void setDataSourceFormat(String value) {
		if( value.equals("deccomma") ) {
			this.dataSourceFormat = CSVFormat.DECIMAL_COMMA;
		} else {
			this.dataSourceFormat = CSVFormat.DECIMAL_POINT;
		}
		
	}
	
	
}
