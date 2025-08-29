package service;

import model.bean.AuditLog;
import model.bean.Product;
import model.bean.Purchase;
import model.bean.PurchaseLine;
import model.bean.StockMovement;
import model.bean.Supplier;
import model.bean.Warehouse;
import model.dao.AuditLogsDAO;
import model.dao.PurchaseLinesDAO;
import model.dao.PurchasesDAO;
import model.dao.StockMovementsDAO;
import model.dao.StocksDAO;

public class PurchasesService {
	PurchasesDAO dao = new PurchasesDAO();
	PurchaseLinesDAO purchaseLinesDAO = new PurchaseLinesDAO();
	StocksDAO stocksDAO = new StocksDAO();
	StockMovementsDAO stockMovementsDAO = new StockMovementsDAO();
	AuditLogsDAO auditLogsDAO = new AuditLogsDAO();

	public int quickReceive(Product product, Supplier supplier, Warehouse warehouse, int qty) {
		int purchaseId = dao.insert(new Purchase(supplier.getId(), "RECEIVED"));
		PurchaseLine pl = new PurchaseLine(purchaseId, product.getId(), qty);
		purchaseLinesDAO.insert(pl);
		stocksDAO.upQty(new PurchaseLine(pl);
		stockMovementsDAO.insertMovement(new StockMovement(product.getId(), warehouse.getId(), qty, "PURCHASE", "PURCHASE", purchaseId));
		auditLogsDAO.insertLog(new AuditLog("RECIEVE", "purchase", purchaseId));
		return purchaseId;
	}

	public int insertSlip(Purchase purchases) {
		int insertId = 0;
		insertId = dao.insertPurchase(purchases.getSupplierId(), purchases.getStatu());
		return insertId;
	}
}
