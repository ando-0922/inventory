package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.InventoryConnection;

public class SalesDAO {
	InventoryConnection inventoryConnection = new InventoryConnection();

	public int insertSale(String customerNote){
		String sql = "INSERT INTO sales(customer_note) VALUES (?) RETURNING id";
		try (Connection con = inventoryConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, customerNote);
			try(ResultSet rs = ps.executeQuery();){
				rs.next();
				return rs.getInt(1);
				
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return 0;
	}
}
