package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.bean.AuditLog;
import model.bean.PendingLine;
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

public class OrdersController{

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
			switch (menu.dispOrdersMenu()) {
			case "5":
				System.out.print("仕入先 (ID/キーワード) (必須): ");
				String supKey2 = sc.nextLine().trim();
				Supplier supForOrder = null;
				if (supKey2.matches("\\d+"))
					supForOrder = findSupplierById(suppliers, Long.parseLong(supKey2));
				else
					supForOrder = findSupplierByNamePartial(suppliers, supKey2);
				if (supForOrder == null) {
					System.out.println("仕入先が見つかりません");
					break;
				}

				System.out.print("倉庫ID (必須): ");
				String whOrd = sc.nextLine().trim();
				if (!whOrd.matches("\\d+")) {
					System.out.println("倉庫IDを正しく入力してください");
					break;
				}
				long whOrderId = Long.parseLong(whOrd);
				Warehouse whOrder = findWarehouse(warehouses, whOrderId);
				if (whOrder == null) {
					System.out.println("倉庫が見つかりません");
					break;
				}

				List<PendingLine> tmpLines = new ArrayList<>();
				while (true) {
					System.out.print("JAN [空で終了]: ");
					String j = sc.nextLine().trim();
					if (j.isEmpty())
						break;
					Product prd = findProductByJan(products, j);
					if (prd == null) {
						System.out.println("登録されているJANを入力してください。");
						continue;
					}
					System.out.print("数量 (整数>0) (必須): ");
					int q;
					try {
						q = Integer.parseInt(sc.nextLine().trim());
					} catch (NumberFormatException e) {
						System.out.println("数量は整数で入力してください");
						continue;
					}
					if (q <= 0) {
						System.out.println("数量は1以上を入力してください");
						continue;
					}
					tmpLines.add(new PendingLine(prd.id, prd.jan, prd.name, q));
					System.out.printf("明細追加: %s %s x %d\n", prd.jan, prd.name, q);
				}
				if (tmpLines.isEmpty()) {
					System.out.println("明細がありません。処理を中止します。");
					break;
				}

				System.out.println("##一覧表示 ##");
				tmpLines.forEach(l -> System.out.printf("%s %s x %d\n", l.jan, l.name, l.qty));
				System.out.print("この内容で発注を作成しますか？ (y/N): ");
				String okOrd = sc.nextLine().trim();
				if (!okOrd.equalsIgnoreCase("y")) {
					System.out.println("キャンセル");
					break;
				}

				long newPoId = ++purchaseSeq;
				purchases.add(new Purchase(newPoId, supForOrder.id, "ORDERED", LocalDate.now(), null));

				int added = 0;
				for (PendingLine pl : tmpLines) {
					long newPlId2 = ++purchaseLineSeq;
					purchaseLines.add(new PurchaseLine(newPlId2, newPoId, pl.productId, pl.qty, 0));
					// No stock change here (ORDERED)
					added++;
				}
				auditLogs.add(new AuditLog("ORDER_CREATE", "purchases", newPoId, LocalDate.now()));
				System.out.printf("OK: 発注を作成しました (purchase_id=%d, 明細=%d) [状態: ORDERED]\n", newPoId, added);
				break;
			case "6":
				System.out.print("発注ID (必須): ");
				String pidStr = sc.nextLine().trim();
				if (!pidStr.matches("\\d+")) {
					System.out.println("IDを正しく入力してください");
					break;
				}
				long pid = Long.parseLong(pidStr);
				Purchase target = findPurchase(purchases, pid);
				if (target == null) {
					System.out.println("ERROR: 対象が見つかりません");
					break;
				}

				// list lines
				List<PurchaseLine> lines = findPurchaseLinesByPurchaseId(purchaseLines, pid);
				if (lines.isEmpty()) {
					System.out.println("明細が存在しません");
					break;
				}
				System.out.println("明細一覧 (ordered / received / remain):");
				for (PurchaseLine pl : lines) {
					Product pp = findProduct(products, pl.productId);
					System.out.printf("LineID:%d product:%s name:%s ordered:%d received:%d remain:%d\n",
							pl.id, pp != null ? pp.jan : pl.productId, pp != null ? pp.name : "N/A",
							pl.orderedQty, pl.receivedQty, (pl.orderedQty - pl.receivedQty));
				}

				System.out.print("受入倉庫 (ID) (必須): ");
				String rwh = sc.nextLine().trim();
				if (!rwh.matches("\\d+")) {
					System.out.println("倉庫IDを正しく入力してください");
					break;
				}
				long rwhId = Long.parseLong(rwh);
				Warehouse rwhObj = findWarehouse(warehouses, rwhId);
				if (rwhObj == null) {
					System.out.println("倉庫が見つかりません");
					break;
				}

				Map<Long, Integer> recvInput = new HashMap<>();
				int totalInput = 0;
				for (PurchaseLine pl : lines) {
					int remain = pl.orderedQty - pl.receivedQty;
					if (remain <= 0) {
						recvInput.put(pl.id, 0);
						continue;
					}
					System.out.printf("#%d：受入数量 [0..%d] : ", pl.id, remain);
					String rStr = sc.nextLine().trim();
					int rQty = 0;
					try {
						rQty = Integer.parseInt(rStr);
					} catch (NumberFormatException e) {
						System.out.println("整数で入力してください");
						rQty = -1;
					}
					if (rQty < 0 || rQty > remain) {
						System.out.println("ERROR: 受入数量が発注残を超えています");
						recvInput.clear();
						break;
					}
					recvInput.put(pl.id, rQty);
					totalInput += rQty;
				}
				if (recvInput.isEmpty()) {
					System.out.println("受入入力が不正で中止");
					break;
				}
				if (totalInput == 0) {
					System.out.println("ERROR: 入力がすべて0です");
					break;
				}

				// Confirm list
				System.out.println("##一覧表示 ##");
				for (PurchaseLine pl : lines) {
					int r = recvInput.getOrDefault(pl.id, 0);
					Product ppp = findProduct(products, pl.productId);
					System.out.printf("商品:%s %s 受入:%d\n", ppp != null ? ppp.jan : pl.productId,
							ppp != null ? ppp.name : "N/A", r);
				}
				System.out.print("この内容で検収しますか？ (y/N): ");
				String conf = sc.nextLine().trim();
				if (!conf.equalsIgnoreCase("y")) {
					System.out.println("キャンセル");
					break;
				}

				// Apply receives
				for (PurchaseLine pl : lines) {
					int r = recvInput.getOrDefault(pl.id, 0);
					if (r <= 0)
						continue;
					pl.receivedQty += r;
					// add stock
					Stock s = findOrCreateStock(stocks, pl.productId, rwhId);
					s.qty += r;
					// add movement
					long mid2 = ++movementSeq;
					movements.add(new StockMovement(mid2, pl.productId, rwhId, r, "PURCHASE", "PURCHASE", pid,
							LocalDate.now()));
				}

				// update header status if all lines fully received
				boolean allReceived = true;
				int remainSum = 0;
				for (PurchaseLine pl : lines) {
					int rem = pl.orderedQty - pl.receivedQty;
					remainSum += rem;
					if (rem > 0)
						allReceived = false;
				}
				if (allReceived) {
					target.status = "RECEIVED";
					target.receivedAt = LocalDate.now();
				}
				auditLogs.add(new AuditLog("RECEIVE", "purchases", pid, LocalDate.now()));
				System.out.printf("OK: 検収を登録しました（発注残: %d）[状態: %s]\n", remainSum, target.status);
				if (allReceived)
					System.out.println("※残ゼロのため発注ヘッダを RECEIVED にしました。");

				break;
			case "7":
				System.out.print("対象倉庫 [全部]: ");
				String tw = sc.nextLine().trim();
				Long targetWh = null;
				if (!tw.isEmpty() && !tw.equalsIgnoreCase("全部")) {
					try {
						targetWh = Long.parseLong(tw);
					} catch (Exception e) {
						System.out.println("倉庫ID不正");
						break;
					}
				}
				System.out.print("需要予測日数 [既定=0]: ");
				String daysStr = sc.nextLine().trim();
				int forecastDays = 0;
				if (!daysStr.isEmpty()) {
					try {
						forecastDays = Integer.parseInt(daysStr);
					} catch (NumberFormatException e) {
						System.out.println("不正な数値");
						break;
					}
				}
				// generate suggested lines
				long newReplId = ++replSeq;
				replenishments.add(new Replenishment(newReplId, "DRAFT", null));
				int createdLines = 0;
				for (Product pd : products) {
					// compute stock across warehouses or only targetWh
					int totalStock = totalStockOfProduct(stocks, pd.id, targetWh);
					int orderRemain = totalOrderRemainForProduct(purchaseLines, purchases, pd.id);
					int demand = 0; // demand prediction is out-of-scope; leave 0
					int net = totalStock + orderRemain - demand;
					if (net < pd.reorderPoint) {
						// round up to order_lot
						int need = pd.reorderPoint - net;
						int lot = pd.orderLot > 0 ? pd.orderLot : 1;
						int suggested = ((need + lot - 1) / lot) * lot;
						long newReplLineId = ++replLineSeq;
						replenishmentLines
								.add(new ReplenishmentLine(newReplLineId, newReplId, pd.id, suggested, null));
						createdLines++;
					}
				}
				System.out.printf("##一覧表示 ## (対象商品数=%d / 生成予定行数=%d)\n", products.size(), createdLines);
				System.out.print("この内容で作成しますか？ (y/N): ");
				String ok7 = sc.nextLine().trim();
				if (!ok7.equalsIgnoreCase("y")) {
					// rollback: remove created repl and its lines
					replenishments.removeIf(r -> r.id == newReplId);
					replenishmentLines.removeIf(rl -> rl.replId == newReplId);
					System.out.println("キャンセルしました。");
				} else {
					auditLogs.add(new AuditLog("REPL_CREATE", "replenishments", newReplId, LocalDate.now()));
					System.out.printf("OK: 発注案を作成しました (id=%d, 明細=%d) [状態: DRAFT]\n", newReplId, createdLines);
				}
				break;
			case "8":
				System.out.print("発注案ID (必須): ");
				String ridStr = sc.nextLine().trim();
				if (!ridStr.matches("\\d+")) {
					System.out.println("ID不正");
					break;
				}
				long rid = Long.parseLong(ridStr);
				Replenishment repl = findReplenishment(replenishments, rid);
				if (repl == null) {
					System.out.println("発注案が見つかりません");
					break;
				}
				if (!repl.status.equals("DRAFT")) {
					System.out.println("発注案がDRAFTではありません");
					break;
				}

				List<ReplenishmentLine> replLines = findReplLinesByReplId(replenishmentLines, rid);
				if (replLines.isEmpty()) {
					System.out.println("明細がありません");
					break;
				}

				// display and collect approved qty
				Map<Long, Integer> approvedMap = new HashMap<>();
				for (ReplenishmentLine rl : replLines) {
					Product rp = findProduct(products, rl.productId);
					int suggested = rl.suggestedQty;
					System.out.printf("商品: %s (%s) suggested=%d\n", rp != null ? rp.name : rl.productId,
							rp != null ? rp.jan : "N/A", suggested);
					System.out.printf("承認数量 [既定=%d] [Enter=既定]: ", suggested);
					String aStr = sc.nextLine().trim();
					int approved = aStr.isEmpty() ? suggested : Integer.parseInt(aStr);
					approved = Math.max(0, approved);
					approvedMap.put(rl.id, approved);
				}
				System.out.print("承認して発注を作成しますか？ (y/N): ");
				String ok8 = sc.nextLine().trim();
				if (!ok8.equalsIgnoreCase("y")) {
					System.out.println("キャンセル");
					break;
				}

				// Ask supplier for the created purchases (simple flow: ask supplier id)
				System.out.print("発注先仕入先IDを入力してください (必須): ");
				String supForRepl = sc.nextLine().trim();
				Supplier supSel = null;
				if (supForRepl.matches("\\d+"))
					supSel = findSupplierById(suppliers, Long.parseLong(supForRepl));
				if (supSel == null) {
					System.out.println("仕入先が見つかりません");
					break;
				}

				long newPurchaseFromReplId = ++purchaseSeq;
				purchases.add(new Purchase(newPurchaseFromReplId, supSel.id, "ORDERED", LocalDate.now(), null));
				int createdPLCount = 0;
				for (ReplenishmentLine rl : replLines) {
					int appr = approvedMap.getOrDefault(rl.id, rl.suggestedQty);
					if (appr <= 0)
						continue;
					long newPlId = ++purchaseLineSeq;
					purchaseLines.add(new PurchaseLine(newPlId, newPurchaseFromReplId, rl.productId, appr, 0));
					createdPLCount++;
				}
				// mark replenishment approved
				repl.status = "APPROVED";
				repl.approvedAt = LocalDate.now();
				auditLogs.add(new AuditLog("REPL_APPROVE", "replenishments", rid));
				System.out.printf("OK: 承認しました (purchase_id=%d, 明細=%d) [発注: ORDERED / 案: APPROVED]\n",
						newPurchaseFromReplId, createdPLCount);

				break;
			
			default:
				System.out.println("０～11の数字を入力してください。");
			}
		}
	}
	
}