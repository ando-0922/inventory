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

public class SuppliersController{

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
			switch (menu.dispSuppliersMenu()) {
			
			case "10":
				System.out.print("1) 登録  2) 更新    選択: ");
				String m10 = sc.nextLine().trim();
				if (m10.equals("1")) {
					System.out.print("名称 (必須): ");
					String nm = sc.nextLine().trim();
					System.out.print("リードタイム(日)(必須): ");
					int lead = Integer.parseInt(sc.nextLine().trim());
					System.out.print("電話 (必須): ");
					String tel = sc.nextLine().trim();
					System.out.print("Email (必須): ");
					String mail = sc.nextLine().trim();
					long newSid = ++supplierSeq;
					suppliers.add(new Supplier(newSid, nm, lead, tel, mail));
					auditLogs.add(new AuditLog("SUPPLIER_CREATE", "suppliers", newSid, LocalDate.now()));
					System.out.printf("OK: 登録しました (id=%d)\n", newSid);
				} else if (m10.equals("2")) {
					System.out.print("検索（ID/キーワード）: ");
					String key = sc.nextLine().trim();
					Supplier ss = null;
					if (key.matches("\\d+"))
						ss = findSupplierById(suppliers, Long.parseLong(key));
					else
						ss = findSupplierByNamePartial(suppliers, key);
					if (ss == null) {
						System.out.println("該当なし");
						break;
					}
					System.out.printf("現値: name=%s lead=%d phone=%s email=%s\n", ss.name, ss.leadTimeDays, ss.phone,
							ss.email);
					System.out.print("名称 [" + ss.name + "]: ");
					String n1 = sc.nextLine().trim();
					if (!n1.isEmpty())
						ss.name = n1;
					System.out.print("リードタイム(日) [" + ss.leadTimeDays + "]: ");
					String n2 = sc.nextLine().trim();
					if (!n2.isEmpty())
						ss.leadTimeDays = Integer.parseInt(n2);
					System.out.print("電話 [" + ss.phone + "]: ");
					String n3 = sc.nextLine().trim();
					if (!n3.isEmpty())
						ss.phone = n3;
					System.out.print("Email [" + ss.email + "]: ");
					String n4 = sc.nextLine().trim();
					if (!n4.isEmpty())
						ss.email = n4;
					auditLogs.add(new AuditLog("SUPPLIER_UPDATE", "suppliers", ss.id));
					System.out.println("OK: 更新しました");
				} else {
					System.out.println("選択が不正です");
				}
				break;
			
			default:
				disp.dispMsg("前に戻ります");
				return;
			}
		}
	}
	
}