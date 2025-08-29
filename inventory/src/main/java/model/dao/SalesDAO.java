package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.InventoryConnection;

public class SalesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertSale(String customerNote) throws SQLException {
		String sql = "INSERT INTO sales(customer_note) VALUES (?) RETURNING id";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setString(1, customerNote);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt("id");
		}
	}
}
