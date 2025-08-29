package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import model.InventoryConnection;

public class ReplenishmentLinesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public void insertLine(int replId, int productId, int suggestedQty){
		String sql = "INSERT INTO replenishment_lines(repl_id, product_id, suggested_qty) VALUES (?, ?, ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, replId);
			ps.setInt(2, productId);
			ps.setInt(3, suggestedQty);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void updateApprovedQty(int lineId, int approvedQty){
		String sql = "UPDATE replenishment_lines SET approved_qty=? WHERE id=?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, approvedQty);
			ps.setInt(2, lineId);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public Map<Integer, Integer> getApprovedQtyMap(int replId) throws SQLException {
		// lineId→approvedQty
		// ここはCLI入力と組み合わせて実装するよてい
		return null;
	}
}
