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

public class PurchaseLinesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insert(PurchaseDetail pd) {
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
	public void insertLine(int purchaseId, String productId, int qty) throws SQLException {
		String sql = "INSERT INTO purchase_lines(purchase_id, product_id, ordered_qty) VALUES (?, ?, ?)";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, purchaseId);
			ps.setString(2, productId);
			ps.setInt(3, qty);
			ps.executeUpdate();
		}
	}

	public void insertLine(int purchaseId, int productId, int orderedQty, int receivedQty) throws SQLException {
		String sql = "INSERT INTO purchase_lines(purchase_id, product_id, ordered_qty, received_qty) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, purchaseId);
			ps.setInt(2, productId);
			ps.setInt(3, orderedQty);
			ps.setInt(4, receivedQty);
			ps.executeUpdate();
		}
	}

	public List<PurchaseDetail> getLines(int purchaseId) throws SQLException {
		String sql = "SELECT * FROM purchase_lines WHERE purchase_id=?";
		List<PurchaseDetail> list = new ArrayList<>();
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, purchaseId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PurchaseDetail pd = new PurchaseDetail();
				pd.setId(rs.getInt("id"));
				pd.setPurchaseId(rs.getInt("purchase_id"));
				pd.setProductId(rs.getInt("product_id"));
				pd.setOrderedQty(rs.getInt("ordered_qty"));
				pd.setReceivedQty(rs.getInt("received_qty"));
				list.add(pd);
			}
		}
		return list;
	}

	public void addReceived(int lineId, int qty) throws SQLException {
		String sql = "UPDATE purchase_lines SET received_qty = received_qty + ? WHERE id=?";
		try (PreparedStatement ps = inventoryConnection.prepareStatement(sql)) {
			ps.setInt(1, qty);
			ps.setInt(2, lineId);
			ps.executeUpdate();
		}
	}

}
