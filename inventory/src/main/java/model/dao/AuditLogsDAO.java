package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.InventoryConnection;
import model.bean.AuditLog;
import model.bean.JSONObject;

public class AuditLogsDAO {

	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertLog(AuditLog a) {
		String sql = "INSERT INTO audit_logs(actor, action, entity, entity_id, detail) VALUES (?, ?, ?, ?, ?::jsonb)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, a.getActor());
			ps.setString(2, a.getAction());
			ps.setString(3, a.getEntity());
			ps.setInt(4, a.getEntityId());
			JSONObject json = new JSONObject();
			json.put("data", data);
			json.put("qty", qty);
			ps.setString(5, json.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
