package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.InventoryConnection;
import model.bean.PurchaseLine;
import model.bean.StockMovement;
import model.bean.StorageHistory;

public class StockMovementsDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public List<StorageHistory> allHistory() {
		String sql = "SELECT p.name, sm.qty, sm.type, sm.ref_type, sm.moved_at \n"
				+ "FROM stock_movements sm \n"
				+ "JOIN products p ON sm.product_id = p.id";
		var list = new ArrayList<StorageHistory>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					int qty = rs.getInt("qty");
					String type = rs.getString("type");
					String refType = rs.getString("ref_type");
					Timestamp movedAt = rs.getTimestamp("moved_at");
					list.add(new StorageHistory(name, qty, type, refType, movedAt));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<StorageHistory> historyById(int selectId) {
		String sql = "SELECT p.name, sm.qty, sm.type, sm.ref_type, sm.moved_at \n"
				+ "FROM stock_movements sm \n"
				+ "JOIN products p ON sm.product_id = p.id\n"
				+ "WHERE sm.warehouse_id = ?";
		var list = new ArrayList<StorageHistory>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, selectId);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					int qty = rs.getInt("qty");
					String type = rs.getString("type");
					String refType = rs.getString("ref_type");
					Timestamp movedAt = rs.getTimestamp("moved_at");
					list.add(new StorageHistory(name, qty, type, refType, movedAt));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int changeType(PurchaseLine pd, int warehouseId) {
		String sql = "INSERT INTO stock_movements\n"
				+ "(product_id,warehouse_id,qty,type,ref_type,ref_id,moved_at)\n"
				+ "VALUES(?,?,?,?,?,?,NOW())";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, pd.getProductId());
			ps.setInt(2, warehouseId);
			ps.setInt(3, pd.getReceivedQty());
			ps.setString(4, "PURCHASE");
			ps.setString(5, "PURCHASE");
			ps.setInt(6, pd.getPurchaseId());
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

	public int insertMovement(StockMovement s) {
		String sql = "INSERT INTO stock_movements(product_id, warehouse_id, qty, type, ref_type, ref_id) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
			ps.setInt(1, s.getProductId());
			ps.setInt(2, s.getWarehouseId());
			ps.setInt(3, s.getQty());
			ps.setString(4, s.getType());
			ps.setString(5, s.getRefType());
			ps.setInt(6, s.getRefId());
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


}
