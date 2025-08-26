package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.InventoryConnection;
import model.bean.Supplier;

public class SuppliersDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public String srchSupById(int id) {
		String sql = "SELECT name FROM suppliers WHERE id = ?";
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

	public String srchSupByWrd(String keyword) {
		String sql = "SELECT name FROM suppliers WHERE id LIKE ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, "%"+keyword+"%");
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

	public int insertS(Supplier sup) {
		String sql = "INSERT INTO suppliers\n"
				+ "(name,lead_time_days,phone,email,created_at,updated_at)\n"
				+ "VALUES(?,?,?,?,NOW(),NOW())";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, sup.getName());
			ps.setInt(2, sup.getLeadTimeDays());
			ps.setString(3, sup.getPhone());
			ps.setString(4, sup.getEmail());
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

	public int srchId(int supId) {
		String sql = "SELECT id FROM suppliers WHERE id = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, supId);
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

	public int srchIdByKey(String supkey) {
		String sql = "SELECT id FROM suppliers WHERE name LIKE ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, "%" + supkey + "%");
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

	public int updateS(Supplier sup, int resultId) {
		String sql = "UPDATE suppliers SET\n"
				+ "name = ?, lead_time_days = ?, phone = ?,\n"
				+ "email = ?, updated_at = NOW() WHERE id = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, sup.getName());
			ps.setInt(2, sup.getLeadTimeDays());
			ps.setString(3, sup.getPhone());
			ps.setString(4, sup.getEmail());
			ps.setInt(5, resultId);
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
