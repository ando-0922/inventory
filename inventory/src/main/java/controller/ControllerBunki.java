package controller;

import view.Displayer;

public class ControllerBunki {
	public static void main(String[] args) {

		Displayer disp = new Displayer();
		while (true) {
			String select = disp.dispMenuAndSelect();
			disp.dispMenuOnCase(select);
			switch (select) {
			case "1", "2" -> {
				StocksController stocksController = new StocksController();
				stocksController.root();
			}
			case "3" -> {PurchasesController purchasesController = new PurchasesController();
				PurchaseController();}
			case "4" -> {SalesController salesController = new SalesController();
				SalesController();
			}
			case "5", "6", "7", "8" -> {OrdersController ordersController = new OrdersController();
				OrdersController();
			}
			case "9" -> {ProductsController productsController = new ProductsController();
				ProductsController();
			}
			case "10" -> {SuppliersController suppliersController = new SuppliersController();
				SuppliersController();
			}
			case "11" -> {ReportsController reportsController = new ReportsController();
				ReportController();
			}
			case "0" -> disp.dispMsg("終了します。");
			default -> disp.dispMsg("０～11の数字を入力してください。");
			}
		}
	}
}