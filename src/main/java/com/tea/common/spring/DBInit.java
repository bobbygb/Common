package com.tea.common.spring;

import javax.sql.DataSource;

public class DBInit {

	
	public static DataSource createNewDataSource()
	{	
		return new org.apache.tomcat.dbcp.dbcp.BasicDataSource();
	}
	
	
}
