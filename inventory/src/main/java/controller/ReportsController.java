package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import view.OriginalDisplayer;

public class ReportsController{

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
		while (true) {
			String select = disp.dispMenuAndSelect();
			disp.dispMenuOnCase(select);
			switch (select) {
			case "11":
				//				System.out.println("== 11. レポートCSV出力 ==");
				//				System.out.println("レポート種別 [sales-ranking|stockout|stock-turnover] (必須): _");
				//				System.out.println("from (YYYY-MM-DD) (必須): _ ");
				//				System.out.println("to (YYYY-MM-DD) (必須): _（種別により不要な場合はスキップ）");
				//				System.out.println("OK: 出力しました ./exports/{filename}.csv");
				System.out.print("レポート種別 [sales-ranking|stockout|stock-turnover] (必須): ");
				String rtype = sc.nextLine().trim();
				System.out.print("from (YYYY-MM-DD) (必須): ");
				LocalDate rFrom;
				try {
					rFrom = LocalDate.parse(sc.nextLine().trim());
				} catch (Exception e) {
					System.out.println("日付不正");
					break;
				}
				System.out.print("to (YYYY-MM-DD) (必須): ");
				LocalDate rTo;
				try {
					rTo = LocalDate.parse(sc.nextLine().trim());
				} catch (Exception e) {
					System.out.println("日付不正");
					break;
				}
				if (rFrom.isAfter(rTo)) {
					System.out.println("ERROR: 期間不正");
					break;
				}

				File dir = new File("./exports");
				if (!dir.exists())
					dir.mkdirs();
				String filename = "exports/report_" + System.currentTimeMillis() + ".csv";
				try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
					if (rtype.equals("sales-ranking")) {
						// aggregate saleLines by product in date range
						Map<Long, Integer> sold = new HashMap<>();
						for (SaleLine sl : saleLines) {
							Sale sh = findSale(sales, sl.saleId);
							if (sh == null)
								continue;
							if (sh.soldAt.isBefore(rFrom) || sh.soldAt.isAfter(rTo))
								continue;
							sold.put(sl.productId, sold.getOrDefault(sl.productId, 0) + sl.qty);
						}
						pw.println("product_jan,product_name,sold_qty");
						List<Map.Entry<Long, Integer>> list = new ArrayList<>(sold.entrySet());
						list.sort((a, b) -> b.getValue() - a.getValue());
						for (var e : list) {
							Product pp = findProduct(products, e.getKey());
							pw.printf("%s,%s,%d\n", pp != null ? pp.jan : e.getKey(), pp != null ? pp.name : "N/A",
									e.getValue());
						}
					} else if (rtype.equals("stockout")) {
						pw.println("product_jan,product_name,total_stock");
						for (Product pp : products) {
							int tot = totalStockOfProduct(stocks, pp.id, null);
							if (tot <= 0)
								pw.printf("%s,%s,%d\n", pp.jan, pp.name, tot);
						}
					} else if (rtype.equals("stock-turnover")) {
						pw.println("product_jan,product_name,sold_qty,current_stock,turnover_rate");
						for (Product pp : products) {
							int soldQty = 0;
							for (SaleLine sl : saleLines) {
								Sale sh = findSale(sales, sl.saleId);
								if (sh == null)
									continue;
								if (sh.soldAt.isBefore(rFrom) || sh.soldAt.isAfter(rTo))
									continue;
								if (sl.productId == pp.id)
									soldQty += sl.qty;
							}
							int curStock = totalStockOfProduct(stocks, pp.id, null);
							double rate = curStock > 0 ? (double) soldQty / curStock
									: (soldQty > 0 ? Double.POSITIVE_INFINITY : 0.0);
							pw.printf("%s,%s,%d,%d,%.3f\n", pp.jan, pp.name, soldQty, curStock, rate);
						}
					} else {
						System.out.println("不明な種別");
						break;
					}
					System.out.println("OK: 出力しました ./" + filename);
				} catch (Exception e) {
					System.out.println("ERROR: CSV出力失敗: " + e.getMessage());
				}
				break;
			
			default:
				System.out.println("０～11の数字を入力してください。");
			}
		}
	}
	
}