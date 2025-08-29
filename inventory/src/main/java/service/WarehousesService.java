package service;

import model.bean.Warehouse;
import model.dao.WarehousesDAO;

public class WarehousesService {

	public Warehouse warehouse(int inpPcWarehouseId) {
		WarehousesDAO warehouseDao = new WarehousesDAO();
		Warehosue wh = warehouseDao.srchById(inpPcWarehouseId);
	}

}
