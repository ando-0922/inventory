package model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.InventoryConnection;

public class AuditLogsDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	//auditlog
	public void insertLog(String action, String message, int refId, String productId, int qty) throws SQLException {
		String sql = "INSERT INTO audit_logs(action, message, ref_id, product_id, qty) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setString(1, action);
			ps.setString(2, message);
			ps.setInt(3, refId);
			ps.setString(4, productId);
			ps.setInt(5, qty);
			ps.executeUpdate();
		}
	}
	 public void insertLog(String action, String entity, String table, int entityId, Object data, int qty) throws SQLException {
	        String sql = "INSERT INTO audit_logs(actor, action, entity, entity_id, detail) VALUES (?, ?, ?, ?, ?::jsonb)";
	        try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
	            ps.setString(1, "SYSTEM");
	            ps.setString(2, action);
	            ps.setString(3, table);
	            ps.setInt(4, entityId);

	            JSONObject json = new JSONObject();
	            json.put("data", data);
	            json.put("qty", qty);

	            ps.setString(5, json.toString());
	            ps.executeUpdate();
	        }
	    }
}
