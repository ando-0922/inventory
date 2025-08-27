package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

}
