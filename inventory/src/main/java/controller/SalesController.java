package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.bean.AuditLog;
import model.bean.Product;
import model.bean.Purchase;
import model.bean.PurchaseLine;
import model.bean.Replenishment;
import model.bean.ReplenishmentLine;
import model.bean.Sale;
import model.bean.SaleLine;
import model.bean.Stock;
import model.bean.StockMovement;
import model.bean.Supplier;
import model.bean.Warehouse;
import service.AuditLogsService;
import service.ProductsService;
import service.PurchaseLinesService;
import service.PurchasesService;
import service.SalesService;
import service.StockMovementsService;
import service.StocksService;
import service.SuppliersService;
import service.WarehousesService;
import view.Displayer;
import view.MenuDisplayer;
import view.OriginalDisplayer;

public class SalesController{

	public void select() {
		//printf  
		//% 書式指定子開始
		//d 整数10進数　s文字列　c文字
		//値:最小桁数指定　.値:文字列の最大幅を指定
		//-:左詰め　0:0埋める
		Scanner scan = new Scanner(System.in);
		List<Product> products = new ArrayList<>();
		List<Supplier> suppliers = new ArrayList<>();
		List<Warehouse> warehouses = new ArrayList<>();
		List<Stock> stocks = new ArrayList<>(); // (productId, warehouseId) -> qty
		List<Purchase> purchases = new ArrayList<>();
		List<PurchaseLine> purchaseLines = new ArrayList<>();
		List<Replenishment> replenishments = new ArrayList<>();
		List<ReplenishmentLine> replenishmentLines = new ArrayList<>();
		List<Sale> sales = new ArrayList<>();
		List<SaleLine> saleLines = new ArrayList<>();
		List<StockMovement> movements = new ArrayList<>();
		List<AuditLog> auditLogs = new ArrayList<>();
		//		Service service = new Service();
		StocksService stocksSvc = new StocksService();
		StockMovementsService stockmovementsSvc = new StockMovementsService();
		SuppliersService suppliersSvc = new SuppliersService();
		WarehousesService warehousesSvc = new WarehousesService();
		ProductsService productsSvc = new ProductsService();
		PurchasesService purchasesSvc = new PurchasesService();
		PurchaseLinesService purchaselinesSvc = new PurchaseLinesService();
		SalesService salesSvc = new SalesService();
		SaleLinesService salelinesSvc = new SaleLinesService();
		AuditLogsService auditlogsSvc = new AuditLogsService();
		Displayer disp = new Displayer();
		OriginalDisplayer oDisp = new OriginalDisplayer();
		MenuDisplayer menu = new MenuDisplayer();
		while (true) {
			String select = menu.dispPurchasesMenu();
			switch (select) {
			
			case "4":
				Warehouse whSale = warehousesSvc.warehouse(disp.entIntNotNull("倉庫 (ID) (必須): _"));
				Warehouse whObj = findWarehouse(warehouses, whSale.getId());
				if (whObj == null) {
					disp.dispMsg("倉庫が見つかりません");
					break;
				}
				Product slProduct = productsSvc.jan(oDisp.entJanNotNull("JAN (必須): _"));
				if (slProduct == null) {
					disp.dispMsg("商品が見つかりません");
					break;
				}
				Stock cur = stocksSvc.findStock(stocks, slProduct.getId(), whSale.getId());
				int curQty = cur != null ? cur.qty : 0;
				disp.dispMsg("現在庫: " + curQty);
				int saleQty = disp.entIntNotNull("数量 (整数>0) (必須): ");
				if (curQty < saleQty) {
					disp.dispMsg("在庫不足");
					break;
				}
				int priceInput = disp.entIntNotNull("売価 ");
				disp.dispMsg("##出庫確認##");
				disp.dispFormat("倉庫: %s / 商品: %s %s\n数量: %d / 単価: %.2f / 金額: %.2f\n",
						whObj.name, pSale.jan, pSale.name, saleQty, priceInput, saleQty * unitPrice);
				if (!disp.confirm("この内容で実行しますか？ (y/N): ")) {
					System.out.println("キャンセル");
					break;
				}

				// create sale header + line
				long newSaleId = ++saleSeq;
				sales.add(new Sale(newSaleId, LocalDate.now(), ""));
				long newSaleLineId = ++saleLineSeq;
				saleLines.add(new SaleLine(newSaleLineId, newSaleId, pSale.id, saleQty, unitPrice));

				// deduct stock
				cur.qty -= saleQty;

				// stock movement
				long movId = ++movementSeq;
				movements.add(new StockMovement(movId, pSale.id, whSaleId, -saleQty, "SALE", "SALE", newSaleId,
						LocalDate.now()));

				// audit log
				auditLogs.add(new AuditLog("SALE", "sales", newSaleId);

				System.out.printf("OK: 出庫しました (sale_id=%d, movement_id=%d, 新在庫=%d)\n", newSaleId, movId, cur.qty);

				break;
			
			default:
				disp.dispMsg("前に戻ります。");
				return;
			}
		}
	}
	public Warehouse findWarehouse(List<Warehouse> ws, long id) {
		for (Warehouse w : ws)
			if (w.getId() == id)
				return w;
		return null;
	}
}