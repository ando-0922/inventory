package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.InventoryConnection;

public class SaleLinesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertLine(int saleId, int productId, int qty, double unitPrice){
		String sql = "INSERT INTO sale_lines(sale_id, product_id, qty, unit_price) VALUES (?, ?, ?, ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, saleId);
			ps.setInt(2, productId);
			ps.setInt(3, qty);
			ps.setDouble(4, unitPrice);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return 0;
	}
}
