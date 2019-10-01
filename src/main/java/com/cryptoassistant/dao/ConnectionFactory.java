package com.cryptoassistant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

	public Connection getConnection() throws SQLException {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_cryptoassistant", "root", "root");	
			return con;
		} catch (ClassNotFoundException ex) {
			throw new SQLException(ex);
		}
		
	}
}
