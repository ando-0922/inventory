package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.InventoryConnection;
import model.bean.AuditLog;

public class AuditLogsDAO {

	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertLog(AuditLog a) {
		String sql = "INSERT INTO audit_logs(actor, action, entity, entity_id, detail) VALUES (?, ?, ?, ?, ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, a.getActor());
			ps.setString(2, a.getAction());
			ps.setString(3, a.getEntity());
			ps.setInt(4, a.getEntityId());
			ps.setString(5, a.getDetail());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
