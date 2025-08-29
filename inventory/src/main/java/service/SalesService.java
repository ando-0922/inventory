package service;

import java.time.LocalDate;

import model.bean.Product;
import model.bean.SaleDetail;
import model.bean.Warehouse;
import model.dao.AuditLogsDAO;
import model.dao.SaleLinesDAO;
import model.dao.SalesDAO;
import model.dao.StockMovementsDAO;

public class SalesService {

	SalesDAO sldao = new SalesDAO();

	public int insertSlip(SaleDetail saledetail) {
		return sldao.insert(saledetail);
	}

	private SalesDAO salesDAO = new SalesDAO();
	private SaleLinesDAO saleLinesDAO = new SaleLinesDAO();
	private StockMovementsDAO stockMovementsDAO = new StockMovementsDAO();
	private AuditLogsDAO auditLogsDAO = new AuditLogsDAO();
	private StocksService stocksService = new StocksService();

	public int quickSale(Product product, Warehouse warehouse, int qty, int unitPrice) {
		// 在庫チェック 
		int stock = stocksService.getQty(product, warehouse);
		if (stock < qty)
			throw new RuntimeException("在庫不足です");
		// ヘッダ作成 
		Sale sale = new Sale(LocalDate.now());
		int saleId = salesDAO.insert(sale);
		// 明細作成 
		SaleDetail detail = new SaleDetail(saleId, product.getId(), qty, unitPrice);
		saleLinesDAO.insert(detail);
		// 在庫減算 
		stocksService.subtractQty(product, warehouse, qty);
		// 在庫変遷履歴作成 
		StockMovement sm = new StockMovement(product.getId(), warehouse.getId(), -qty, "SALE", "SALE", saleId);
		stockMovementsDAO.insert(sm);
		// 監査ログ登録 
		auditLogsDAO.insert("SALE", "sales", saleId, null);
		return saleId;
	}


}
