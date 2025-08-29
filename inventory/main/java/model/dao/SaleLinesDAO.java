package model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.InventoryConnection;

public class SaleLinesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public void insertLine(int saleId, int productId, int qty, double unitPrice) throws SQLException {
		String sql = "INSERT INTO sale_lines(sale_id, product_id, qty, unit_price) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, saleId);
			ps.setInt(2, productId);
			ps.setInt(3, qty);
			ps.setDouble(4, unitPrice);
			ps.executeUpdate();
		}
	}
}
