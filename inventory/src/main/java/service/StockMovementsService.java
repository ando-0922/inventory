package service;

import java.util.List;

import model.bean.StorageHistory;
import model.dao.StockMovementsDAO;

public class StockMovementsService {

	StockMovementsDAO smdao = new StockMovementsDAO();
	public List<StorageHistory> historyList(String inpWarehouseHis) {//case2
		List<StorageHistory>list = null;
		if (inpWarehouseHis.equals("全部")) {
			list = smdao.allHistory();
		} else if (inpWarehouseHis.matches("^\\d+$")) {
			list = smdao.historyById(Integer.parseInt(inpWarehouseHis));
		}
		return list;
	}

	public int insertHistory(StorageHistory storHistory) {
		return smdao.insertMovement(storHistory);
	}
}
