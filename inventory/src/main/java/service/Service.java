package service;

import java.util.List;

import model.bean.Stock;
import model.bean.StorageHistory;
import model.dao.StockDAO;
import model.dao.StockMovementsDAO;

public class Service {

	public List<Stock> stockList(String warehouse) {//case1
		StockDAO stockdao = new StockDAO();
		List<Stock>list = null;
		if (warehouse.equals("全部")) {
			list = stockdao.allStocks();
		} else if (warehouse.matches("^\\d+$")) {
			list = stockdao.stocksById(Integer.parseInt(warehouse));
		}
		return list;
	}

	public List<StorageHistory> historyList(String inpWarehouseHis) {//case2
		StockMovementsDAO smdao = new StockMovementsDAO();
		List<StorageHistory>list = null;
		if (inpWarehouseHis.equals("全部")) {
			list = smdao.allHistory();
		} else if (inpWarehouseHis.matches("^\\d+$")) {
			list = smdao.historyById(Integer.parseInt(inpWarehouseHis));
		}
		return list;
	}

}
