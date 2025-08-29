package service;

import model.bean.Product;
import model.dao.ProductsDAO;

public class ProductsService {
	ProductsDAO dao = new ProductsDAO();

	public Product jan(String inpPcJan) {
		return dao.getByJan(inpPcJan);
	}

}
