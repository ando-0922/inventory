package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.InventoryConnection;
import model.bean.PurchaseDetail;
import model.bean.PurchaseSlip;
import model.bean.Stock;

public class StocksDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public List<Stock> allStocks() {
		String sql = "SELECT p.name,s.warehouse_id,w.name AS warehouse,s.qty FROM stocks s \n"
				+ "JOIN products p ON s.product_id = p.id\n"
				+ "JOIN warehouses w ON s.warehouse_id = w.id";
		var list = new ArrayList<Stock>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					int warehouseId = rs.getInt("warehouse_id");
					String warehouse = rs.getString("warehouse");
					int qty = rs.getInt("qty");
					list.add(new Stock(name, warehouseId, warehouse, qty));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Stock> stocksById(int selectId) {
		String sql = "SELECT p.name,s.warehouse_id,w.name AS warehouse,s.qty \n"
				+ "FROM stocks s \n"
				+ "JOIN products p ON s.product_id = p.id\n"
				+ "JOIN warehouses w ON s.warehouse_id = w.id\n"
				+ "WHERE s.warehouse_id = ?";
		var list = new ArrayList<Stock>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, selectId);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					int warehouseId = rs.getInt("warehouse_id");
					String warehouse = rs.getString("warehouse");
					int qty = rs.getInt("qty");
					list.add(new Stock(name, warehouseId, warehouse, qty));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int upQty(PurchaseDetail pd, PurchaseSlip p) {
		String sql = "UPDATE stocks SET qty = ?\n"
				+ "WHERE product_id = ? AND warehouse_id = \n"
				+ "(SELECT id FROM warehouses WHERE name = ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, pd.getReceivedQty());
			ps.setInt(2, pd.getProductId());
			ps.setString(3, p.getWarehouse());
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
	  public void addStock(String productId, int warehouseId, int qty) throws SQLException {
	        String updateSql = "UPDATE stocks SET qty = qty + ? WHERE product_id = ? AND warehouse_id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
	            ps.setInt(1, qty);
	            ps.setString(2, productId);
	            ps.setInt(3, warehouseId);
	            int updated = ps.executeUpdate();
	            if (updated == 0) {
	                String insertSql = "INSERT INTO stocks(product_id, warehouse_id, qty) VALUES (?, ?, ?)";
	                try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
	                    ps2.setString(1, productId);
	                    ps2.setInt(2, warehouseId);
	                    ps2.setInt(3, qty);
	                    ps2.executeUpdate();
	                }
	            }
	        }
	    }
}
