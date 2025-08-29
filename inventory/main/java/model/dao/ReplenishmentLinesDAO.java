package model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import model.InventoryConnection;

public class ReplenishmentLinesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public void insertLine(int replId, int productId, int suggestedQty) throws SQLException {
		String sql = "INSERT INTO replenishment_lines(repl_id, product_id, suggested_qty) VALUES (?, ?, ?)";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, replId);
			ps.setInt(2, productId);
			ps.setInt(3, suggestedQty);
			ps.executeUpdate();
		}
	}

	public void updateApprovedQty(int lineId, int approvedQty) throws SQLException {
		String sql = "UPDATE replenishment_lines SET approved_qty=? WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, approvedQty);
			ps.setInt(2, lineId);
			ps.executeUpdate();
		}
	}

	public Map<Integer, Integer> getApprovedQtyMap(int replId) throws SQLException {
		// lineId→approvedQty
		// ここはCLI入力と組み合わせて実装するよてい
		return null;
	}
}
