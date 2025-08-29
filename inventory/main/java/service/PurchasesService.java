package service;

import model.bean.Product;
import model.bean.PurchaseDetail;
import model.bean.Warehouse;

public class PurchasesService {
	public int quickReceive(Product product, Supplier supplier, Warehouse warehouse, int qty) {
		int purchaseId = purchasesDAO.insert(new Purchase(supplier.getId(), "RECEIVED"));
		purchaseLinesDAO.insert(new PurchaseDetail(purchaseId, product.getId(), qty));
		stocksDAO.addQty(product, warehouse, qty);
		stockMovementsDAO
				.insert(new StockMovement(product.getId(), warehouse.getId(), qty, "PURCHASE", "PURCHASE", purchaseId));
		auditLogsDAO.insert(new AuditLog("RECIEVE", "purchase", purchaseId));
		return purchaseId;
	}
}
