package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.InventoryConnection;
import model.bean.Supplier;

public class SuppliersDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public Supplier srchIdNameById(int serchId) {
		String sql = "SELECT id,name FROM suppliers WHERE id = ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, serchId);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					return new Supplier(id, name);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Supplier srchIdNameByWrd(String keyword) {
		String sql = "SELECT id,name FROM suppliers WHERE id LIKE ?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setString(1, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					return new Supplier(id, name);
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

	public int insertSupplier(String name, int leadTime, String phone, String email) {
		String sql = "INSERT INTO suppliers(name, lead_time_days, phone, email) VALUES (?, ?, ?, ?) RETURNING id";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, name);
			ps.setInt(2, leadTime);
			ps.setString(3, phone);
			ps.setString(4, email);
			try (ResultSet rs = ps.executeQuery();) {
				rs.next();
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return 0;
	}

	public int updateSupplier(int id, String name, int leadTime, String phone, String email) {
		String sql = "UPDATE suppliers SET name=?, lead_time_days=?, phone=?, email=? WHERE id=?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, name);
			ps.setInt(2, leadTime);
			ps.setString(3, phone);
			ps.setString(4, email);
			ps.setInt(5, id);
			ps.executeUpdate();
			try (ResultSet rs = ps.executeQuery();) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return 0;
	}

	public Supplier getById(int id) {
		String sql = "SELECT * FROM suppliers WHERE id=?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					Supplier s = new Supplier();
					s.setId(rs.getInt("id"));
					s.setName(rs.getString("name"));
					s.setLeadTimeDays(rs.getInt("lead_time_days"));
					s.setPhone(rs.getString("phone"));
					s.setEmail(rs.getString("email"));
					return s;
				}
			}
			return null;
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public List<Supplier> search(String keyword) {
		String sql = "SELECT * FROM suppliers WHERE name ILIKE ?";
		List<Supplier> list = new ArrayList<>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					Supplier s = new Supplier();
					s.setId(rs.getInt("id"));
					s.setName(rs.getString("name"));
					list.add(s);
				}
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return list;

	}
}
