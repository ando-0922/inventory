package service;

import java.util.List;

import model.bean.Product;
import model.bean.Stock;
import model.bean.Warehouse;
import model.dao.StocksDAO;

public class StocksService {
	StocksDAO dao = new StocksDAO();

	// 現在庫取得 
	public int getQty(Product product, Warehouse warehouse) {
		return dao.getQty(product.getId(), warehouse.getId());
	}

	// 在庫加算 
	public void addQty(Product product, Warehouse warehouse, int qty) {
		int current = getQty(product, warehouse);
		if (current == -1) {
			// レコードなしなら作成
			dao.insert(product.getId(), warehouse.getId(), qty);
		} else {
			dao.update(product.getId(), warehouse.getId(), current + qty);
		}
	}

	// 在庫減算 
	public void subtractQty(Product product, Warehouse warehouse, int qty) {
		int current = getQty(product, warehouse);
		if (current < qty) {
			throw new RuntimeException("在庫不足です");
		}
		dao.update(product.getId(), warehouse.getId(), current - qty);
	}

	public List<Stock> stockList(String warehouse) {//case1
		List<Stock> list = null;
		if (warehouse.equals("全部")) {
			list = dao.allStocks();
		} else if (warehouse.matches("^\\d+$")) {
			list = dao.stocksById(Integer.parseInt(warehouse));
		}
		return list;
	}

	public void updateQty(Product pcProduct, Warehouse warehouse, int pcQty) {
		dao.upQty(pcProduct, warehouse, pcQty);
	}

	public Stock findStock(List<Stock> stocks, int id, int id2) {
		for (Stock s : stocks) {
			if (s.getProductId() == id && s.getWarehouseId() == id2) {
				return s;
			}
		}
		return null;

	}
}
