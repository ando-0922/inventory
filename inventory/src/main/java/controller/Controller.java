package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
import model.bean.StorageHistory;
import model.bean.Supplier;
import model.bean.Warehouse;
import service.PurchasesService;
import service.SalesService;
import service.StockMovementsService;
import service.StocksService;
import service.SuppliersService;
import service.WarehousesService;
import view.Displayer;
import view.OriginalDisplayer;

public class Controller {
	public static void main(String[] args) {
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
			case "1":
				String inpPickWarehouse = oDisp.srchWordWarehouse();//検索倉庫を入力
				List<Stock> stockList = stocksSvc.stockList(inpPickWarehouse);
				String inpPartOfName = disp.entStr("キーワード [空=全件] : ");
				oDisp.stockListbrunchOnNull(stockList, inpPartOfName);//在庫の一覧表示
				break;
			case "2":
				String inpWarehouseHis = oDisp.srchWordWarehouse();//検索倉庫を入力
				List<StorageHistory> historyList = stockmovementsSvc.historyList(inpWarehouseHis);//指定した倉庫の履歴の検索結果
				String hisType = oDisp.entHisType();//区別を入力
				LocalDate period[] = oDisp.entPeriod();//指定する期間を入力
				oDisp.dispHistory(historyList, hisType, period);//履歴を表示
				break;
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
				System.out.println("現在庫: " + curQty);
				System.out.print("数量 (整数>0) (必須): ");
				int saleQty;
				try {
					saleQty = Integer.parseInt(sc.nextLine().trim());
				} catch (NumberFormatException e) {
					System.out.println("ERROR: 数量は整数で入力してください");
					break;
				}
				if (saleQty <= 0) {
					System.out.println("ERROR: 数量は1以上を入力してください");
					break;
				}
				if (curQty < saleQty) {
					System.out.println("ERROR: 在庫不足");
					break;
				}

				System.out.printf("売価 [既定=%.2f]: ", pSale.stdPrice);
				String priceInput = sc.nextLine().trim();
				double unitPrice = priceInput.isEmpty() ? pSale.stdPrice : Double.parseDouble(priceInput);

				System.out.println("##出庫確認##");
				System.out.printf("倉庫: %s / 商品: %s %s\n数量: %d / 単価: %.2f / 金額: %.2f\n",
						whObj.name, pSale.jan, pSale.name, saleQty, unitPrice, saleQty * unitPrice);
				System.out.print("この内容で実行しますか？ (y/N): ");
				String okSale = sc.nextLine().trim();
				if (!okSale.equalsIgnoreCase("y")) {
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
				auditLogs.add(new AuditLog("SALE", "sales", newSaleId, LocalDate.now()));

				System.out.printf("OK: 出庫しました (sale_id=%d, movement_id=%d, 新在庫=%d)\n", newSaleId, movId, cur.qty);

				break;
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
				auditLogs.add(new AuditLog("REPL_APPROVE", "replenishments", rid, LocalDate.now()));
				System.out.printf("OK: 承認しました (purchase_id=%d, 明細=%d) [発注: ORDERED / 案: APPROVED]\n",
						newPurchaseFromReplId, createdPLCount);

				break;
			case "9":
				System.out.print("1) 登録  2) 更新    選択: ");
				String m9 = sc.nextLine().trim();
				if (m9.equals("1")) {
					System.out.print("JAN (必須): ");
					String j = sc.nextLine().trim();
					if (findProductByJan(products, j) != null) {
						System.out.println("ERROR: 同一JANの商品が既に存在します");
						break;
					}
					System.out.print("商品名 (必須): ");
					String nm = sc.nextLine().trim();
					System.out.print("標準原価 (>=0) (必須): ");
					double stdc = Double.parseDouble(sc.nextLine().trim());
					System.out.print("標準売価 (>=0) (必須): ");
					double stdp = Double.parseDouble(sc.nextLine().trim());
					System.out.print("発注点 (>=0) (必須): ");
					int rpnt = Integer.parseInt(sc.nextLine().trim());
					System.out.print("発注ロット (>=1) (必須): ");
					int lot = Integer.parseInt(sc.nextLine().trim());
					long newPid = ++productSeq;
					products.add(new Product(newPid, j, nm, stdc, stdp, rpnt, lot, false));
					auditLogs.add(new AuditLog("PRODUCT_CREATE", "products", newPid, LocalDate.now()));
					System.out.printf("OK: 登録しました (id=%d)\n", newPid);
				} else if (m9.equals("2")) {
					System.out.print("検索（JAN/キーワード）: ");
					String key = sc.nextLine().trim();
					List<Product> found = searchProduct(products, key);
					if (found.isEmpty()) {
						System.out.println("該当なし");
						break;
					}
					Product selP = found.get(0);
					System.out.printf(
							"現値: name=%s std_cost=%.2f std_price=%.2f reorder=%d lot=%d discontinued=%b\n",
							selP.name, selP.stdCost, selP.stdPrice, selP.reorderPoint, selP.orderLot,
							selP.discontinued);
					System.out.print("商品名 [" + selP.name + "]: ");
					String nname = sc.nextLine().trim();
					if (!nname.isEmpty())
						selP.name = nname;
					System.out.print("標準原価 [" + selP.stdCost + "]: ");
					String ns = sc.nextLine().trim();
					if (!ns.isEmpty())
						selP.stdCost = Double.parseDouble(ns);
					System.out.print("標準売価 [" + selP.stdPrice + "]: ");
					String ns2 = sc.nextLine().trim();
					if (!ns2.isEmpty())
						selP.stdPrice = Double.parseDouble(ns2);
					System.out.print("発注点 [" + selP.reorderPoint + "]: ");
					String ns3 = sc.nextLine().trim();
					if (!ns3.isEmpty())
						selP.reorderPoint = Integer.parseInt(ns3);
					System.out.print("発注ロット [" + selP.orderLot + "]: ");
					String ns4 = sc.nextLine().trim();
					if (!ns4.isEmpty())
						selP.orderLot = Integer.parseInt(ns4);
					System.out.print("廃番フラグ (true/false) [" + selP.discontinued + "]: ");
					String ns5 = sc.nextLine().trim();
					if (!ns5.isEmpty())
						selP.discontinued = Boolean.parseBoolean(ns5);
					auditLogs.add(new AuditLog("PRODUCT_UPDATE", "products", selP.id, LocalDate.now()));
					System.out.println("OK: 更新しました");
				} else {
					System.out.println("選択が不正です");
				}
				break;
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
					auditLogs.add(new AuditLog("SUPPLIER_UPDATE", "suppliers", ss.id, LocalDate.now()));
					System.out.println("OK: 更新しました");
				} else {
					System.out.println("選択が不正です");
				}
				break;
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
			case "0":
				System.out.println("終了します。");
				break;
			default:
				System.out.println("０～11の数字を入力してください。");
			}
		}
	}

	static Product findProduct(List<Product> products, long id) {
		for (Product p : products)
			if (p.id == id)
				return p;
		return null;
	}

	static Product findProductByJan(List<Product> products, String jan) {
		for (Product p : products)
			if (p.jan.equals(jan))
				return p;
		return null;
	}

	static List<Product> searchProduct(List<Product> products, String key) {
		List<Product> r = new ArrayList<>();
		for (Product p : products)
			if (p.jan.contains(key) || p.name.contains(key))
				r.add(p);
		return r;
	}

	static Warehouse findWarehouse(List<Warehouse> ws, long id) {
		for (Warehouse w : ws)
			if (w.id == id)
				return w;
		return null;
	}

	static Stock findOrCreateStock(List<Stock> stocks, long productId, long warehouseId) {
		Stock s = findStock(stocks, productId, warehouseId);
		if (s != null)
			return s;
		Stock n = new Stock(productId, warehouseId, 0);
		stocks.add(n);
		return n;
	}

	static Purchase findPurchase(List<Purchase> purchases, long id) {
		for (Purchase p : purchases)
			if (p.id == id)
				return p;
		return null;
	}

	static List<PurchaseLine> findPurchaseLinesByPurchaseId(List<PurchaseLine> pls, long purchaseId) {
		List<PurchaseLine> out = new ArrayList<>();
		for (PurchaseLine p : pls)
			if (p.purchaseId == purchaseId)
				out.add(p);
		return out;
	}

	static int totalStockOfProduct(List<Stock> stocks, long productId, Long warehouseFilter) {
		int sum = 0;
		for (Stock s : stocks)
			if (s.getProductId() == productId && (warehouseFilter == null || s.warehouseId == warehouseFilter))
				sum += s.qty;
		return sum;
	}

	static int totalOrderRemainForProduct(List<PurchaseLine> pls, List<Purchase> purchases, long productId) {
		// sum of (ordered - received) for ORDERED purchases
		Set<Long> orderedPurchaseIds = new HashSet<>();
		for (Purchase pur : purchases)
			if ("ORDERED".equals(pur.status))
				orderedPurchaseIds.add(pur.id);
		int sum = 0;
		for (PurchaseLine pl : pls)
			if (orderedPurchaseIds.contains(pl.purchaseId) && pl.productId == productId)
				sum += (pl.orderedQty - pl.receivedQty);
		return sum;
	}

	static Replenishment findReplenishment(List<Replenishment> reps, long id) {
		for (Replenishment r : reps)
			if (r.id == id)
				return r;
		return null;
	}

	static List<ReplenishmentLine> findReplLinesByReplId(List<ReplenishmentLine> rls, long replId) {
		List<ReplenishmentLine> out = new ArrayList<>();
		for (ReplenishmentLine rl : rls)
			if (rl.replId == replId)
				out.add(rl);
		return out;
	}

	static Sale findSale(List<Sale> sales, long id) {
		for (Sale s : sales)
			if (s.id == id)
				return s;
		return null;
	}
}