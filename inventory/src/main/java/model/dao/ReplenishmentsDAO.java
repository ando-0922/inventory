package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.InventoryConnection;

public class ReplenishmentsDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertReplenishment() {
		String sql = "INSERT INTO replenishments(status) VALUES ('DRAFT') RETURNING id";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			try (ResultSet rs = ps.executeQuery();) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return 0;
	}

	public int approveReplenishment(int replId) {
		String sql = "UPDATE replenishments SET status='APPROVED', approved_at=NOW() WHERE id=?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, replId);
			ps.executeUpdate();
			try (ResultSet rs = ps.executeQuery();) {
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
