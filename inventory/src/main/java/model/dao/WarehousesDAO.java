package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.InventoryConnection;

public class WarehousesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public String srchById(int id) {
		String sql = "SELECT name FROM warehouses WHERE id = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, id);
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

}
