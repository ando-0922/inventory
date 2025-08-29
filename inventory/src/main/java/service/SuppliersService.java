package service;

import java.util.List;

import model.bean.Supplier;
import model.dao.SuppliersDAO;

public class SuppliersService {

	SuppliersDAO spdao = new SuppliersDAO();

	public Supplier supplier(String shire) {
		Supplier sp;
		if (shire.matches("^\\d+$")) {
			sp = spdao.srchIdNameById(Integer.parseInt(shire));
		} else {
			sp = spdao.srchIdNameByWrd(shire);
		}
		return sp;
	}

	public int insrtSup(Supplier sup) {
		return spdao.insertS(sup);
	}

	public int srchId(String supkey) {
		if (supkey.matches("^\\d+$")) {
			int supId = Integer.parseInt(supkey);
			return spdao.srchId(supId);
		} else {
			return spdao.srchIdByKey(supkey);
		}
	}

	public Supplier findSupplier(List<Supplier> suppliers, String supKey) {
		if (supKey.matches("\\d+")) {
			return findSupplierById(suppliers, Long.parseLong(supKey));
		} else {
			return findSupplierByNamePartial(suppliers, supKey);
		}
	}
	public Supplier findSupplierById(List<Supplier> suppliers, long id) {
		for (Supplier s : suppliers)
			if (s.getId() == id)
				return s;
		return null;
	}

	public Supplier findSupplierByNamePartial(List<Supplier> suppliers, String key) {
		for (Supplier s : suppliers)
			if (s.getName().contains(key))
				return s;
		return null;
	}
}
