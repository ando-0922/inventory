package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import model.InventoryConnection;
import model.bean.PurchaseSlip;

public class PurchasesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insert(PurchaseSlip p) {
		String sql = "INSERT INTO purchases(supplier_id,ordered_at,received_at,status)\n"
				+ "VALUES(?,NOW(),NOW(),?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, p.getSupplierId());
			ps.setString(2, p.getStatu());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public int insertPurchase(int supplierId, String status) throws SQLException {
		String sql = "INSERT INTO purchases(supplier_id, status) VALUES (?, ?) RETURNING id";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, supplierId);
			ps.setString(2, status);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				throw new SQLException("purchase_id取得失敗");
			}
		}
	}
	//    public int insertPurchase(int supplierId, String status) throws SQLException {
	//        String sql = "INSERT INTO purchases(supplier_id, status) VALUES (?, ?) RETURNING id";
	//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	//            ps.setInt(1, supplierId);
	//            ps.setString(2, status);
	//            ResultSet rs = ps.executeQuery();
	//            rs.next();
	//            return rs.getInt("id");
	//        }
	//    }

	// 発注案から発注作成
	public int insertPurchaseFromRepl(int replId, Map<Integer, Integer> approvedQtyMap) throws SQLException {
		String sql = "INSERT INTO purchases(supplier_id, status) SELECT supplier_id, 'ORDERED' FROM replenishments WHERE id=? RETURNING id";
		int purchaseId;
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, replId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			purchaseId = rs.getInt("id");
		}

		String lineSql = "INSERT INTO purchase_lines(purchase_id, product_id, ordered_qty, received_qty) VALUES (?, ?, ?, 0)";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(lineSql)) {
			for (Map.Entry<Integer, Integer> e : approvedQtyMap.entrySet()) {
				ps.setInt(1, purchaseId);
				ps.setInt(2, e.getKey());
				ps.setInt(3, e.getValue());
				ps.addBatch();
			}
			ps.executeBatch();
		}
		return purchaseId;
	}

	public void updateStatus(int purchaseId, String status) throws SQLException {
		String sql = "UPDATE purchases SET status=? WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, purchaseId);
			ps.executeUpdate();
		}
	}

	public PurchaseSlip getPurchaseById(int purchaseId) throws SQLException {
		String sql = "SELECT * FROM purchases WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, purchaseId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				PurchaseSlip p = new PurchaseSlip();
				p.setId(rs.getInt("id"));
				p.setSupplierId(rs.getInt("supplier_id"));
				p.setStatu(rs.getString("status"));
				return p;
			}
			return null;
		}
	}

}
