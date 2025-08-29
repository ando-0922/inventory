package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.InventoryConnection;
import model.bean.PurchaseLine;

public class PurchaseLinesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insert(PurchaseLine pd) {
		String sql = "INSERT INTO purchase_lines\n"
				+ "(purchase_id,product_id,ordered_qty,received_qty)\n"
				+ "VALUES(?,?,?,?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, pd.getPurchaseId());
			ps.setInt(2, pd.getProductId());
			ps.setInt(3, pd.getOrderedQty());
			ps.setInt(4, pd.getReceivedQty());
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

	public int insertLine(int purchaseId, String productId, int qty) {
		String sql = "INSERT INTO purchase_lines(purchase_id, product_id, ordered_qty) VALUES (?, ?, ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, purchaseId);
			ps.setString(2, productId);
			ps.setInt(3, qty);
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

	public int insertLine(int purchaseId, int productId, int orderedQty, int receivedQty) throws SQLException {
		String sql = "INSERT INTO purchase_lines(purchase_id, product_id, ordered_qty, received_qty) VALUES (?, ?, ?, ?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);) {
			ps.setInt(1, purchaseId);
			ps.setInt(2, productId);
			ps.setInt(3, orderedQty);
			ps.setInt(4, receivedQty);
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

	public List<PurchaseLine> getLines(int purchaseId) {
		String sql = "SELECT * FROM purchase_lines WHERE purchase_id=?";
		List<PurchaseLine> list = new ArrayList<>();
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, purchaseId);
			ps.executeQuery();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				while (rs.next()) {
					PurchaseLine pd = new PurchaseLine();
					pd.setId(rs.getInt("id"));
					pd.setPurchaseId(rs.getInt("purchase_id"));
					pd.setProductId(rs.getInt("product_id"));
					pd.setOrderedQty(rs.getInt("ordered_qty"));
					pd.setReceivedQty(rs.getInt("received_qty"));
					list.add(pd);
				}
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return list;
	}

	public int addReceived(int lineId, int qty){
		String sql = "UPDATE purchase_lines SET received_qty = received_qty + ? WHERE id=?";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, qty);
			ps.setInt(2, lineId);
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
