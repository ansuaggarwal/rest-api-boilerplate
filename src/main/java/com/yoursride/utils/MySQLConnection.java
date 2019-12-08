package com.yoursride.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

	String driverName;
	String host;
	String port;
	String dbName; 
	String dbUserName; 
	String dbPassword;
	/**
	 * Return the MYSQL connection object
	 * @return {@link Connection}
	 */
	public Connection getMysqlConnection(){
		
		Connection con = null;
		try {
			Class.forName(this.driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  
		try {
			con=DriverManager.getConnection(  
			"jdbc:mysql://"+this.host+":"+this.port+"/"+this.dbName,this.dbUserName,this.dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return con;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
}
