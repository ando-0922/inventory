package controller;

import java.sql.SQLException;

import model.bean.PurchaseLine;
import model.dao.PurchaseLinesDAO;

public class PurchaseLinesService {
PurchaseLinesDAO dao = new PurchaseLinesDAO();
	public void insertLine(PurchaseLine pcDetail) {
		try {
			dao.insertLine(pcDetail.getPurchaseId(),pcDetail.getProductId(), pcDetail.getOrderedQty(), pcDetail.getReceivedQty());
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

}
