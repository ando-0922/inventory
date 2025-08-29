package service;

import model.bean.Product;
import model.bean.Warehouse;
import model.dao.StocksDAO;

public class StocksService {
	private StocksDAO stocksDAO = new StocksDAO();

	// 現在庫取得 
	public int getQty(Product product, Warehouse warehouse) {
		return stocksDAO.getQty(product.getId(), warehouse.getId());
	}

	// 在庫加算 
	public void addQty(Product product, Warehouse warehouse, int qty) {
		int current = getQty(product, warehouse);
		if (current == -1) {
			// レコードなしなら作成
			stocksDAO.insert(product.getId(), warehouse.getId(), qty);
		} else {
			stocksDAO.update(product.getId(), warehouse.getId(), current + qty);
		}
	}

	// 在庫減算 
	public void subtractQty(Product product, Warehouse warehouse, int qty) {
		int current = getQty(product, warehouse);
		if (current < qty) {
			throw new RuntimeException("在庫不足です");
		}
		stocksDAO.update(product.getId(), warehouse.getId(), current - qty);
	}

}