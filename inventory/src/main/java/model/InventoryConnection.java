package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InventoryConnection {
	public Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url = "jdbc:postgresql://localhost:5432/";
		String db = "inventoryManagement";
		String user = "postgres";
		String password = "password";
		
		try {
			return DriverManager.getConnection(url + db, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}