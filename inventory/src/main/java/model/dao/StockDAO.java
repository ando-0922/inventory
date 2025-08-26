package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.InventoryConnection;
import model.bean.ProductInventory;
import model.bean.PurchaseDetail;
import model.bean.PurchaseSlip;

public class StockDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public List<ProductInventory> allStocks() {
		String sql = "SELECT p.name,s.warehouse_id,w.name AS warehouse,s.qty FROM stocks s \n"
				+ "JOIN products p ON s.product_id = p.id\n"
				+ "JOIN warehouses w ON s.warehouse_id = w.id";
		var list = new ArrayList<ProductInventory>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					int warehouseId = rs.getInt("warehouse_id");
					String warehouse = rs.getString("warehouse");
					int qty = rs.getInt("qty");
					list.add(new ProductInventory(name, warehouseId, warehouse, qty));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ProductInventory> stocksById(int selectId) {
		String sql = "SELECT p.name,s.warehouse_id,w.name AS warehouse,s.qty \n"
				+ "FROM stocks s \n"
				+ "JOIN products p ON s.product_id = p.id\n"
				+ "JOIN warehouses w ON s.warehouse_id = w.id\n"
				+ "WHERE s.warehouse_id = ?";
		var list = new ArrayList<ProductInventory>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, selectId);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					int warehouseId = rs.getInt("warehouse_id");
					String warehouse = rs.getString("warehouse");
					int qty = rs.getInt("qty");
					list.add(new ProductInventory(name, warehouseId, warehouse, qty));
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
}
