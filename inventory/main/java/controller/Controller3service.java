package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.bean.Product;
import model.dao.AuditLogsDAO;
import model.dao.ProductsDAO;
import model.dao.PurchaseLinesDAO;
import model.dao.PurchasesDAO;
import model.dao.ReplenishmentLinesDAO;
import model.dao.ReplenishmentsDAO;
import model.dao.StockMovementsDAO;
import model.dao.StocksDAO;
import model.dao.SuppliersDAO;

public class Controller3service {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ProductsDAO productsDAO = new ProductsDAO();
		SuppliersDAO suppliersDAO = new SuppliersDAO();
		PurchasesDAO purchasesDAO = new PurchasesDAO();
		PurchaseLinesDAO purchaseLinesDAO = new PurchaseLinesDAO();
		StockMovementsDAO stockMovementsDAO = new StockMovementsDAO();

		while (true) {
			System.out.println("== メニュー ==");
			System.out.println("1. 発注起票（ORDERED）");
			System.out.println("3. クイック入荷（即時受入れ）");
			System.out.println("4. クイック販売（即時出庫）");
			System.out.println("8. 発注案承認");
			System.out.println("9. 商品登録");
			System.out.println("10. 仕入先登録");
			System.out.println("0. 終了");
			System.out.print("番号を入力: ");
			int sel = Integer.parseInt(sc.nextLine());

			try {
				switch (sel) {
				case 1 -> {
					System.out.println("== 1. 発注起票（ORDERED）==");
					System.out.print("仕入先ID: ");
					int supId = Integer.parseInt(sc.nextLine());
					int purchaseId = purchasesDAO.insertPurchase(supId, "ORDERED");

					while (true) {
						System.out.print("JAN [空で終了]: ");
						String jan = sc.nextLine();
						if (jan.isEmpty())
							break;
						Product p = productsDAO.getByJan(jan);
						if (p == null) {
							System.out.println("商品が見つかりません");
							continue;
						}
						System.out.print("数量: ");
						int qty = Integer.parseInt(sc.nextLine());
						purchaseLinesDAO.insertLine(purchaseId, p.getId(), qty, 0);
						System.out.printf("明細追加: %s %s x %d%n", p.getJan(), p.getName(), qty);
					}
					System.out.printf("OK: 発注を作成しました (purchase_id=%d) [状態: ORDERED]%n", purchaseId);
				}
				case 3 -> {
					System.out.println("== 3. クイック入荷 ==");
					System.out.print("仕入先ID: ");
					int supId = Integer.parseInt(sc.nextLine());
					System.out.print("倉庫ID: ");
					int whId = Integer.parseInt(sc.nextLine());
					System.out.print("JAN: ");
					String jan = sc.nextLine();
					Product p = productsDAO.getByJan(jan);
					if (p == null) {
						System.out.println("商品が見つかりません");
						continue;
					}
					System.out.print("数量: ");
					int qty = Integer.parseInt(sc.nextLine());

					int purchaseId = purchasesDAO.insertPurchase(supId, "RECEIVED");
					purchaseLinesDAO.insertLine(purchaseId, p.getId(), qty, qty);
					stockMovementsDAO.insertMovement(p.getId(), whId, qty, "PURCHASE", "PURCHASE", purchaseId);
					System.out.printf("OK: 入荷処理しました (purchase_id=%d, 商品=%s, 数量=%d)%n", purchaseId, p.getName(), qty);
				}
				case 4 -> {
					System.out.println("== 4. クイック販売 ==");
					System.out.print("倉庫ID: ");
					int whId = Integer.parseInt(sc.nextLine());
					System.out.print("JAN: ");
					String jan = sc.nextLine();
					Product p = productsDAO.getByJan(jan);
					if (p == null) {
						System.out.println("商品が見つかりません");
						continue;
					}
					System.out.print("数量: ");
					int qty = Integer.parseInt(sc.nextLine());
					// TODO 在庫チェック（stocksテーブル参照）
					stockMovementsDAO.insertMovement(p.getId(), whId, -qty, "SALE", "SALE", 0);
					System.out.printf("OK: 販売処理しました (商品=%s, 数量=%d)%n", p.getName(), qty);
				}
				case 8 -> {
					System.out.println("== 8. 発注案承認 ==");
					System.out.println("TODO: replenishmentsテーブルを元に承認処理を実装");
				}
				case 9 -> {
					System.out.println("== 9. 商品登録 ==");
					System.out.print("JAN: ");
					String jan = sc.nextLine();
					System.out.print("商品名: ");
					String name = sc.nextLine();
					System.out.print("原価: ");
					double cost = Double.parseDouble(sc.nextLine());
					System.out.print("売価: ");
					double price = Double.parseDouble(sc.nextLine());
					System.out.print("発注点: ");
					int reorder = Integer.parseInt(sc.nextLine());
					System.out.print("ロット: ");
					int lot = Integer.parseInt(sc.nextLine());
					Product newP = new Product(name, jan, cost, price, reorder, lot);
					int id = productsDAO.insertP(newP);
					System.out.printf("OK: 商品登録しました (id=%d, %s)%n", id, name);
				}
				case 10 -> {
					System.out.println("== 10. 仕入先登録 ==");
					System.out.print("仕入先名: ");
					String name = sc.nextLine();
					System.out.print("リードタイム(日): ");
					int lead = Integer.parseInt(sc.nextLine());
					System.out.print("電話: ");
					String tel = sc.nextLine();
					System.out.print("メール: ");
					String email = sc.nextLine();
					Supplier s = new Supplier(name, lead, tel, email);
					int id = suppliersDAO.insertS(s);
					System.out.printf("OK: 仕入先登録しました (id=%d, %s)%n", id, name);
				}
				case 0 -> {
					System.out.println("終了します");
					return;
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void createPurchase(Scanner sc, Connection conn, String actor) throws SQLException {
		SuppliersDAO supDao = new SuppliersDAO(conn);
		ProductsDAO prodDao = new ProductsDAO(conn);
		PurchasesDAO purDao = new PurchasesDAO(conn);
		PurchaseLinesDAO plDao = new PurchaseLinesDAO(conn);
		StocksDAO stockDao = new StocksDAO(conn);
		StockMovementsDAO smDao = new StockMovementsDAO(conn);
		AuditLogsDAO logDao = new AuditLogsDAO(conn);

		System.out.print("仕入先 (ID/キーワード) (必須): ");
		String supInput = sc.nextLine();
		Supplier sup = supDao.search(supInput).get(0); // 簡略、候補表示省略
		int supplierId = sup.getId();

		int purchaseId = purDao.insertPurchase(supplierId);

		while (true) {
			System.out.print("JAN [空で終了]: ");
			String jan = sc.nextLine();
			if (jan.isEmpty())
				break;
			Product prod = prodDao.getByJan(jan);
			if (prod == null) {
				System.out.println("ERROR: 商品が見つかりません");
				continue;
			}
			System.out.print("数量 (整数>0) (必須): ");
			int qty = Integer.parseInt(sc.nextLine());
			plDao.insertLine(purchaseId, prod.getId(), qty);
			System.out.printf("明細追加: %s x %d%n", prod.getName(), qty);
		}

		System.out.print("この内容で発注を作成しますか？ (y/N): ");
		if (sc.nextLine().equalsIgnoreCase("y")) {
			logDao.insertLog(actor, "ORDER_CREATE", "purchases", purchaseId, Map.of("supplierId", supplierId));
			System.out.printf("OK: 発注を作成しました (purchase_id=%d) [状態: ORDERED]%n", purchaseId);
		}
	}

	public void receivePurchase(Scanner sc, Connection conn, String actor) throws SQLException {
		PurchasesDAO purDao = new PurchasesDAO(conn);
		PurchaseLinesDAO plDao = new PurchaseLinesDAO(conn);
		StocksDAO stockDao = new StocksDAO(conn);
		StockMovementsDAO smDao = new StockMovementsDAO(conn);
		AuditLogsDAO logDao = new AuditLogsDAO(conn);

		System.out.print("発注ID (必須): ");
		int purchaseId = Integer.parseInt(sc.nextLine());
		Purchase pur = purDao.getById(purchaseId);
		if (pur == null) {
			System.out.println("ERROR: 対象が見つかりません");
			return;
		}

		List<PurchaseLine> lines = plDao.getLinesByPurchaseId(purchaseId);
		Map<Integer, Integer> receivedInput = new HashMap<>();
		for (PurchaseLine line : lines) {
			int remain = line.getOrderedQty() - line.getReceivedQty();
			System.out.printf("#%d: 受入数量 [0..%d] : ", line.getId(), remain);
			int r = Integer.parseInt(sc.nextLine());
			if (r < 0 || r > remain) {
				System.out.println("ERROR: 受入数量が発注残を超えています");
				return;
			}
			receivedInput.put(line.getId(), r);
		}

		for (PurchaseLine line : lines) {
			int r = receivedInput.get(line.getId());
			if (r > 0) {
				plDao.addReceivedQty(line.getId(), r);
				stockDao.addStock(line.getProductId(), pur.getWarehouseId(), r);
				smDao.insertMovement(line.getProductId(), pur.getWarehouseId(), r, "PURCHASE", purchaseId);
			}
		}

		boolean allReceived = plDao.isAllReceived(purchaseId);
		if (allReceived)
			purDao.updateStatus(purchaseId, "RECEIVED");

		logDao.insertLog(actor, "RECEIVE", "purchases", purchaseId, Map.of("receivedLines", receivedInput));
		System.out.printf("OK: 検収を登録しました（発注残: %d）[状態: %s]%n", plDao.getRemainQty(purchaseId),
				allReceived ? "RECEIVED" : "ORDERED");
	}

	public void createReplenishmentDraft(Scanner sc, Connection conn, String actor) throws SQLException {
		ReplenishmentsDAO replDao = new ReplenishmentsDAO(conn);
		ReplenishmentLinesDAO lineDao = new ReplenishmentLinesDAO(conn);
		ProductsDAO prodDao = new ProductsDAO(conn);

		System.out.print("対象倉庫 [全部]: ");
		String whInput = sc.nextLine();
		System.out.print("需要予測日数 [既定=0]: ");
		int forecastDays = Integer.parseInt(sc.nextLine().isEmpty() ? "0" : sc.nextLine());

		int replId = replDao.insertReplenishment();

		List<Product> allProducts = prodDao.getAll();
		int n = 0;
		for (Product p : allProducts) {
			int suggested = Math.max(0, p.getReorderPoint() - 0); // 仮: 在庫＋発注残−需要予測
			if (suggested > 0) {
				lineDao.insertLine(replId, p.getId(), suggested);
				n++;
			}
		}

		System.out.print("この内容で作成しますか？ (y/N): ");
		if (sc.nextLine().equalsIgnoreCase("y")) {
			System.out.printf("OK: 発注案を作成しました (id=%d, 明細=%d) [状態: DRAFT]%n", replId, n);
		}
	}

	public void approveReplenishment(Scanner sc, Connection conn, String actor) throws SQLException {
		ReplenishmentsDAO replDao = new ReplenishmentsDAO(conn);
		ReplenishmentLinesDAO lineDao = new ReplenishmentLinesDAO(conn);
		PurchasesDAO purDao = new PurchasesDAO(conn);

		System.out.print("発注案ID (必須): ");
		int replId = Integer.parseInt(sc.nextLine());
		List<ReplenishmentLine> lines = lineDao.getLinesByReplId(replId);

		for (ReplenishmentLine line : lines) {
			System.out.printf("承認数量 [既定=%d] [Enter=既定]: ", line.getSuggestedQty());
			String input = sc.nextLine();
			int approved = input.isEmpty() ? line.getSuggestedQty() : Integer.parseInt(input);
			lineDao.updateApprovedQty(line.getId(), approved);
		}

		System.out.print("承認して発注を作成しますか？ (y/N): ");
		if (sc.nextLine().equalsIgnoreCase("y")) {
			int purchaseId = purDao.createFromReplenishment(replId);
			replDao.approveReplenishment(replId);
			System.out.printf("OK: 承認しました (purchase_id=%d, 明細=%d) [発注: ORDERED / 案: APPROVED]%n", purchaseId,
					lines.size());
		}
	}
}