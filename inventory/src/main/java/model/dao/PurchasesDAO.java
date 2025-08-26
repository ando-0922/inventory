package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.InventoryConnection;
import model.bean.PurchaseSlip;

public class PurchasesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insert(PurchaseSlip p) {
		String sql = "INSERT INTO purchases(supplier_id,ordered_at,received_at,status)\n"
				+ "VALUES(?,NOW(),NOW(),?)";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, p.getSupplierId());
			ps.setString(2, p.getStatu());
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
