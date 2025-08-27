package service;

import java.util.List;

import model.bean.Stock;
import model.dao.StockDAO;

public class Service {

	public List<Stock> stockList(String warehouse) {
		StockDAO stockdao = new StockDAO();
		List<Stock>list = null;
		if (warehouse.equals("全部")) {
			list = stockdao.allStocks();
		} else if (warehouse.matches("^\\d+$")) {
			list = stockdao.stocksById(Integer.parseInt(warehouse));
		}
		return list;
	}

}
