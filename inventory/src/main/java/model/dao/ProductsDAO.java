package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.InventoryConnection;
import model.bean.Product;

public class ProductsDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public Product srchJan(String keyword) {
		String sql = "SELECT name,jan FROM products WHERE jan = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, keyword);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					String name = rs.getString("name");
					String jan = rs.getString("jan");
					return new Product(name,jan);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String srchNameByJan(String resultJan) {

		String sql = "SELECT name FROM products WHERE jan = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, resultJan);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					return rs.getString("name");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public int insertP(Product rp) {
		String sql = "INSERT INTO products\n"
				+ "(jan,name,std_cost,std_price,reorder_point,order_lot,discontinued,created_at,updated_at)\n"
				+ "VALUES(?,?,?,?,?,?,false,NOW(),NOW())";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, rp.getJan());
			ps.setString(2, rp.getName());
			ps.setDouble(3, rp.getStdCost());
			ps.setDouble(4, rp.getStdPrice());
			ps.setInt(5, rp.getReorderPoint());
			ps.setInt(6, rp.getOrderLot());
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

	public int srchIdByJan(String janorkey) {
		String sql = "SELECT id FROM products WHERE jan = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, janorkey);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					return rs.getInt("id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int srchIdByKey(String janorkey) {
		String sql = "SELECT id FROM products WHERE jan LIKE ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, "%" + janorkey + "%");
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					return rs.getInt("id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int updateP(Product rp, int id) {
		String sql = "UPDATE products SET \n"
				+ "name = ?, std_cost = ?, std_price = ?, reorder_point = ?,\n"
				+ "order_lot = ?, updated_at = NOW() WHERE id = ?;";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, rp.getName());
			ps.setDouble(2, rp.getStdCost());
			ps.setDouble(3, rp.getStdPrice());
			ps.setInt(4, rp.getReorderPoint());
			ps.setInt(5, rp.getOrderLot());
			ps.setInt(6, id);
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
