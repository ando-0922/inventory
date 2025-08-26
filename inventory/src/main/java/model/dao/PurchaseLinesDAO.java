package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

}
