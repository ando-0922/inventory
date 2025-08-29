package controller;

import model.bean.AuditLog;
import model.bean.Product;
import model.bean.Purchase;
import model.bean.PurchaseLine;
import model.bean.StorageHistory;
import model.bean.Supplier;
import model.bean.Warehouse;
import service.AuditLogsService;
import service.ProductsService;
import service.PurchaseLinesService;
import service.PurchasesService;
import service.StockMovementsService;
import service.StocksService;
import service.SuppliersService;
import service.WarehousesService;
import view.Displayer;
import view.MenuDisplayer;
import view.OriginalDisplayer;

public class PurchasesController{

	public void select() {
		//printf  
		//% 書式指定子開始
		//d 整数10進数　s文字列　c文字
		//値:最小桁数指定　.値:文字列の最大幅を指定
		//-:左詰め　0:0埋める
		
		//		Service service = new Service();
		StocksService stocksSvc = new StocksService();
		StockMovementsService stockmovementsSvc = new StockMovementsService();
		SuppliersService suppliersSvc = new SuppliersService();
		WarehousesService warehousesSvc = new WarehousesService();
		ProductsService productsSvc = new ProductsService();
		PurchasesService purchasesSvc = new PurchasesService();
		PurchaseLinesService purchaselinesSvc = new PurchaseLinesService();
		AuditLogsService auditlogsSvc = new AuditLogsService();
		Displayer disp = new Displayer();
		OriginalDisplayer oDisp = new OriginalDisplayer();
		MenuDisplayer menu = new MenuDisplayer();
		while (true) {
			switch (menu.dispPurchasesMenu()) {
			case "3":
				Supplier supplier = suppliersSvc.supplier(disp.entStrNotNull("仕入先 (ID/キーワード) (必須): _"));
				Warehouse warehouse = warehousesSvc.warehouse(disp.entIntNotNull("倉庫 (ID) (必須): _"));
				Product pcProduct = productsSvc.jan(oDisp.entJanNotNull("JAN (必須): _"));
				int pcQty = disp.entIntNotNull("数量 (整数>0) (必須): _");
				Purchase purchasesSlip = new Purchase(supplier.getId(), supplier.getName(), warehouse.getName(),
						pcProduct.getJan(), pcProduct.getName(), pcQty);
				disp.dispMsg("##受入確認##\n" + purchasesSlip);
				if (disp.confirm("この内容で実行しますか？ (Y/N): _")) {
					purchasesSlip.setStatu("RECEIVED");//purchase received
					int pcInsertId = purchasesSvc.insertSlip(purchasesSlip);
					PurchaseLine pcDetail = new PurchaseLine(pcInsertId, supplier.getId(), pcProduct.getId());//parchaselines
					purchaselinesSvc.insertLine(pcDetail);
					stocksSvc.updateQty(pcProduct, warehouse, pcQty);//stock
					StorageHistory storHistory = new StorageHistory(pcProduct.getId(), warehouse.getId(), pcQty,
							"PURCHASE", "PURCHASE", pcInsertId);//stockmovements
					int movementId = stockmovementsSvc.insertHistory(storHistory);
					AuditLog purchaseAL = new AuditLog("RECIEVE", "purchase", pcInsertId);//auditlogs
					auditlogsSvc.insertLog(purchaseAL);
					disp.dispMsg("OK: 受入を登録しました。" + pcInsertId + "" + movementId + "" + pcQty);
				} else {
					disp.dispMsg("やり直してください。");
				}
				break;
			
			default:
				disp.dispMsg("前に戻ります。");
			}
		}
	}
	
}