package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.InventoryConnection;
import model.bean.Warehouse;

public class WarehousesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public Warehouse srchById(int srchId) {
		String sql = "SELECT id,name FROM warehouses WHERE id = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, srchId);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					int id = rs.getInt("id");
					String name =  rs.getString("name");
					return new Warehouse(id,name);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int srchByWrd(String keyword) {
		String sql = "SELECT id FROM warehouses WHERE name = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, keyword);
			int getId = 0;
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					getId = rs.getInt("id");
				}
			}
			return getId;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public int insertWarehouse(String name, String location) throws SQLException {
		String sql = "INSERT INTO warehouses(name, location) VALUES (?, ?) RETURNING id";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setString(2, location);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt("id");
		}
	}

	public void updateWarehouse(int id, String name, String location) throws SQLException {
		String sql = "UPDATE warehouses SET name=?, location=? WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setString(2, location);
			ps.setInt(3, id);
			ps.executeUpdate();
		}
	}

	public Warehouse getById(int id) throws SQLException {
		String sql = "SELECT * FROM warehouses WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Warehouse w = new Warehouse();
				w.setId(rs.getInt("id"));
				w.setName(rs.getString("name"));
				w.setLocation(rs.getString("location"));
				return w;
			}
			return null;
		}
	}

	public List<Warehouse> getAll() throws SQLException {
		String sql = "SELECT * FROM warehouses";
		List<Warehouse> list = new ArrayList<>();
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Warehouse w = new Warehouse();
				w.setId(rs.getInt("id"));
				w.setName(rs.getString("name"));
				w.setLocation(rs.getString("location"));
				list.add(w);
			}
		}
		return list;
	}

}
