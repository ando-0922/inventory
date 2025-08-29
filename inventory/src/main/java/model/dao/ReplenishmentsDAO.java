package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.InventoryConnection;

public class ReplenishmentsDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertReplenishment() throws SQLException {
		String sql = "INSERT INTO replenishments(status) VALUES ('DRAFT') RETURNING id";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt("id");
		}
	}

	public void approveReplenishment(int replId) throws SQLException {
		String sql = "UPDATE replenishments SET status='APPROVED', approved_at=NOW() WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, replId);
			ps.executeUpdate();
		}
	}
}
