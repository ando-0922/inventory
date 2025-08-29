import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Controller4 {
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);

		// ===== 仮想テーブル（メモリ） =====
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

		// ID シーケンサ（簡易）
		long productSeq = 0, supplierSeq = 0, warehouseSeq = 0, purchaseSeq = 0, purchaseLineSeq = 0,
				replSeq = 0, replLineSeq = 0, saleSeq = 0, saleLineSeq = 0, movementSeq = 0;

		// 初期倉庫を一つ作る（ID=1）
		warehouses.add(new Warehouse(++warehouseSeq, "本社倉庫"));

		// helper lambdas are implemented as static methods below

		// メインループ
		while (true) {
			System.out.println();
			System.out.println("== 在庫管理 CLI ==");
			System.out.println("1) 在庫参照");
			System.out.println("2) 入出庫履歴");
			System.out.println("3) クイック入荷");
			System.out.println("4) クイック販売");
			System.out.println("5) 発注起票（ORDERED）");
			System.out.println("6) 検収（発注ID指定）");
			System.out.println("7) 発注案 作成（DRAFT）");
			System.out.println("8) 発注案 承認→発注確定");
			System.out.println("9) 商品マスタ 登録/更新");
			System.out.println("10) 仕入先マスタ 登録/更新");
			System.out.println("11) レポートCSV出力");
			System.out.println("0) 終了");
			System.out.print("選択: ");

			String sel = sc.nextLine().trim();
			if (sel.equalsIgnoreCase("end") || sel.equals("0")) {
				System.out.println("終了します。");
				break;
			}
			try {
				switch (sel) {
				// ---------------------------
				case "1": // 在庫参照
					System.out.print("倉庫を選択してください (ID) [全部]: ");
					String whInp = sc.nextLine().trim();
					System.out.print("キーワード [空=全件]: ");
					String kw = sc.nextLine().trim();

					System.out.println("=== 在庫一覧 ===");
					for (Stock s : stocks) {
						if (!whInp.isEmpty() && !whInp.equals("全部")) {
							try {
								long wid = Long.parseLong(whInp);
								if (s.warehouseId != wid)
									continue;
							} catch (NumberFormatException ignored) {
							}
						}
						Product p = findProduct(products, s.productId);
						if (p == null)
							continue;
						if (!kw.isEmpty() && !(p.jan.contains(kw) || p.name.contains(kw)))
							continue;
						Warehouse w = findWarehouse(warehouses, s.warehouseId);
						System.out.printf("JAN:%s / 名称:%s / 倉庫:%s / 在庫:%d\n",
								p.jan, p.name, (w != null ? w.name : s.warehouseId), s.qty);
					}
					break;

				// ---------------------------
				case "2": // 入出庫履歴
					System.out.print("倉庫 (ID) [全部]: ");
					String wh2 = sc.nextLine().trim();
					System.out.print("種別 [ALL|PURCHASE|SALE|ADJUST]: ");
					String type = sc.nextLine().trim();
					System.out.print("期間 From (YYYY-MM-DD) (必須): ");
					LocalDate from;
					try {
						from = LocalDate.parse(sc.nextLine().trim());
					} catch (DateTimeParseException e) {
						System.out.println("ERROR: 日付形式が不正です");
						break;
					}
					System.out.print("期間 To (YYYY-MM-DD) (必須): ");
					LocalDate to;
					try {
						to = LocalDate.parse(sc.nextLine().trim());
					} catch (DateTimeParseException e) {
						System.out.println("ERROR: 日付形式が不正です");
						break;
					}
					if (from.isAfter(to)) {
						System.out.println("ERROR: 期間の指定が不正です");
						break;
					}
					System.out.println("=== 入出庫履歴 ===");
					for (StockMovement m : movements) {
						if (!wh2.isEmpty() && !wh2.equals("全部")) {
							try {
								long wid = Long.parseLong(wh2);
								if (m.warehouseId != wid)
									continue;
							} catch (NumberFormatException ignored) {
							}
						}
						if (!type.isEmpty() && !type.equalsIgnoreCase("ALL") && !m.type.equalsIgnoreCase(type))
							continue;
						if (m.movedAt.isBefore(from) || m.movedAt.isAfter(to))
							continue;
						Product p = findProduct(products, m.productId);
						Warehouse w = findWarehouse(warehouses, m.warehouseId);
						System.out.printf("[%s] %s / 倉庫:%s / 商品:%s / qty:%d / ref:%s(%d) / 日:%s\n",
								m.type, p != null ? p.jan : m.productId, w != null ? w.name : m.warehouseId,
								p != null ? p.name : "N/A", m.qty, m.refType, m.refId, m.movedAt);
					}
					break;

				// ---------------------------
				case "3": // クイック入荷
					System.out.print("仕入先 (ID/キーワード) (必須): ");
					String supKey = sc.nextLine().trim();
					Supplier supplier = null;
					if (supKey.matches("\\d+")) {
						supplier = findSupplierById(suppliers, Long.parseLong(supKey));
					} else {
						supplier = findSupplierByNamePartial(suppliers, supKey);
					}
					if (supplier == null) {
						System.out.println("仕入先が見つかりません。処理を中止します。");
						break;
					}
					System.out.print("倉庫 (ID) (必須): ");
					String whIdStr = sc.nextLine().trim();
					if (!whIdStr.matches("\\d+")) {
						System.out.println("倉庫IDを正しく入力してください");
						break;
					}
					long whId = Long.parseLong(whIdStr);
					Warehouse ware = findWarehouse(warehouses, whId);
					if (ware == null) {
						System.out.println("倉庫が見つかりません");
						break;
					}

					System.out.print("JAN (必須): ");
					String jan = sc.nextLine().trim();
					Product prod = findProductByJan(products, jan);
					if (prod == null) {
						System.out.println("ERROR: 商品が見つかりません");
						break;
					}
					System.out.print("数量 (整数>0) (必須): ");
					int qty;
					try {
						qty = Integer.parseInt(sc.nextLine().trim());
					} catch (NumberFormatException e) {
						System.out.println("ERROR: 数量は整数で入力してください");
						break;
					}
					if (qty <= 0) {
						System.out.println("ERROR: 数量は1以上を入力してください");
						break;
					}

					System.out.println("##受入確認##");
					System.out.printf("仕入先: %s / 倉庫: %s\n商品: %s %s / 数量: %d\n",
							supplier.name, ware.name, prod.jan, prod.name, qty);
					System.out.print("この内容で実行しますか？ (y/N): ");
					String ok = sc.nextLine().trim();
					if (!ok.equalsIgnoreCase("y")) {
						System.out.println("キャンセル");
						break;
					}

					// create purchase header with status=RECEIVED
					long newPurchaseId = ++purchaseSeq;
					purchases.add(new Purchase(newPurchaseId, supplier.id, "RECEIVED", LocalDate.now(), null));

					// purchase line ordered=received=qty
					long newPLId = ++purchaseLineSeq;
					purchaseLines.add(new PurchaseLine(newPLId, newPurchaseId, prod.id, qty, qty));

					// stocks add
					Stock st = findOrCreateStock(stocks, prod.id, whId);
					st.qty += qty;

					// stock movement
					long mid = ++movementSeq;
					movements.add(new StockMovement(mid, prod.id, whId, qty, "PURCHASE", "PURCHASE", newPurchaseId,
							LocalDate.now()));

					// audit log
					auditLogs.add(new AuditLog("RECEIVE", "purchases", newPurchaseId, LocalDate.now()));

					System.out.printf("OK: 受入を登録しました (purchase_id=%d, movement_id=%d, 新在庫=%d)\n",
							newPurchaseId, mid, st.qty);
					break;

				// ---------------------------
				case "4": // クイック販売
					System.out.print("倉庫 (ID) (必須): ");
					String whSale = sc.nextLine().trim();
					if (!whSale.matches("\\d+")) {
						System.out.println("倉庫IDを正しく入力してください");
						break;
					}
					long whSaleId = Long.parseLong(whSale);
					Warehouse whObj = findWarehouse(warehouses, whSaleId);
					if (whObj == null) {
						System.out.println("倉庫が見つかりません");
						break;
					}

					System.out.print("JAN (必須): ");
					String janSale = sc.nextLine().trim();
					Product pSale = findProductByJan(products, janSale);
					if (pSale == null) {
						System.out.println("ERROR: 商品が見つかりません");
						break;
					}
					Stock cur = findStock(stocks, pSale.id, whSaleId);
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

				// ---------------------------
				case "5": // 発注起票（ORDERED）
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

				// ---------------------------
				case "6": // 検収（発注ID指定）
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

				// ---------------------------
				case "7": // 発注案 作成（DRAFT）
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

				// ---------------------------
				case "8": // 発注案 承認 → 発注確定
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

				// ---------------------------
				case "9": // 商品マスタ 登録/更新
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

				// ---------------------------
				case "10": // 仕入先マスタ 登録/更新
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

				// ---------------------------
				case "11": // レポートCSV出力
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
							// simple: sold_qty / avg_stock (avg_stock approximated by (start+end)/2 across period)
							// We'll compute sold_qty in range and avg stock = current stock (approx)
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
			} catch (Exception ex) {
				System.out.println("処理でエラーが発生しました: " + ex.getMessage());
				ex.printStackTrace();
			}
		} // main loop end

		sc.close();
	} // main end

	// ---------------------------
	// Helper functions & simple entity finders
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

	static Supplier findSupplierById(List<Supplier> suppliers, long id) {
		for (Supplier s : suppliers)
			if (s.id == id)
				return s;
		return null;
	}

	static Supplier findSupplierByNamePartial(List<Supplier> suppliers, String key) {
		for (Supplier s : suppliers)
			if (s.name.contains(key))
				return s;
		return null;
	}

	static Warehouse findWarehouse(List<Warehouse> ws, long id) {
		for (Warehouse w : ws)
			if (w.id == id)
				return w;
		return null;
	}

	static Stock findStock(List<Stock> stocks, long productId, long warehouseId) {
		for (Stock s : stocks)
			if (s.productId == productId && s.warehouseId == warehouseId)
				return s;
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
			if (s.productId == productId && (warehouseFilter == null || s.warehouseId == warehouseFilter))
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

	// ---------------------------
	// Simple in-memory entity classes
	static class Product {
		long id;
		String jan;
		String name;
		double stdCost;
		double stdPrice;
		int reorderPoint;
		int orderLot;
		boolean discontinued;

		Product(long id, String jan, String name, double stdCost, double stdPrice, int reorderPoint, int orderLot,
				boolean discontinued) {
			this.id = id;
			this.jan = jan;
			this.name = name;
			this.stdCost = stdCost;
			this.stdPrice = stdPrice;
			this.reorderPoint = reorderPoint;
			this.orderLot = orderLot;
			this.discontinued = discontinued;
		}
	}

	static class Supplier {
		long id;
		String name;
		int leadTimeDays;
		String phone;
		String email;

		Supplier(long id, String name, int leadTimeDays, String phone, String email) {
			this.id = id;
			this.name = name;
			this.leadTimeDays = leadTimeDays;
			this.phone = phone;
			this.email = email;
		}
	}

	static class Warehouse {
		long id;
		String name;

		Warehouse(long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	static class Stock {
		long productId;
		long warehouseId;
		int qty;

		Stock(long p, long w, int q) {
			productId = p;
			warehouseId = w;
			qty = q;
		}
	}

	static class Purchase {
		long id;
		long supplierId;
		String status;
		LocalDate orderedAt;
		LocalDate receivedAt;

		Purchase(long id, long supplierId, String status, LocalDate orderedAt, LocalDate receivedAt) {
			this.id = id;
			this.supplierId = supplierId;
			this.status = status;
			this.orderedAt = orderedAt;
			this.receivedAt = receivedAt;
		}
	}

	static class PurchaseLine {
		long id;
		long purchaseId;
		long productId;
		int orderedQty;
		int receivedQty;

		PurchaseLine(long id, long purchaseId, long productId, int orderedQty, int receivedQty) {
			this.id = id;
			this.purchaseId = purchaseId;
			this.productId = productId;
			this.orderedQty = orderedQty;
			this.receivedQty = receivedQty;
		}
	}

	static class Replenishment {
		long id;
		String status;
		LocalDate approvedAt;

		Replenishment(long id, String status, LocalDate approvedAt) {
			this.id = id;
			this.status = status;
			this.approvedAt = approvedAt;
		}
	}

	static class ReplenishmentLine {
		long id;
		long replId;
		long productId;
		int suggestedQty;
		Integer approvedQty;

		ReplenishmentLine(long id, long replId, long productId, int suggestedQty, Integer approvedQty) {
			this.id = id;
			this.replId = replId;
			this.productId = productId;
			this.suggestedQty = suggestedQty;
			this.approvedQty = approvedQty;
		}
	}

	static class Sale {
		long id;
		LocalDate soldAt;
		String customerNote;

		Sale(long id, LocalDate soldAt, String note) {
			this.id = id;
			this.soldAt = soldAt;
			this.customerNote = note;
		}
	}

	static class SaleLine {
		long id;
		long saleId;
		long productId;
		int qty;
		double unitPrice;

		SaleLine(long id, long saleId, long productId, int qty, double unitPrice) {
			this.id = id;
			this.saleId = saleId;
			this.productId = productId;
			this.qty = qty;
			this.unitPrice = unitPrice;
		}
	}

	static class StockMovement {
		long id;
		long productId;
		long warehouseId;
		int qty;
		String type;
		String refType;
		long refId;
		LocalDate movedAt;

		StockMovement(long id, long productId, long warehouseId, int qty, String type, String refType, long refId,
				LocalDate movedAt) {
			this.id = id;
			this.productId = productId;
			this.warehouseId = warehouseId;
			this.qty = qty;
			this.type = type;
			this.refType = refType;
			this.refId = refId;
			this.movedAt = movedAt;
		}
	}

	static class AuditLog {
		String action;
		String entity;
		long entityId;
		LocalDate loggedAt;

		AuditLog(String action, String entity, long id, LocalDate at) {
			this.action = action;
			this.entity = entity;
			this.entityId = id;
			this.loggedAt = at;
		}
	}

	// helper temporary struct
	static class PendingLine {
		long productId;
		String jan;
		String name;
		int qty;

		PendingLine(long pid, String jan, String name, int qty) {
			this.productId = pid;
			this.jan = jan;
			this.name = name;
			this.qty = qty;
		}
	}
}
