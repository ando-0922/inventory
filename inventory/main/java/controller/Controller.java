package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.bean.AuditLog;
import model.bean.Product;
import model.bean.PurchaseDetail;
import model.bean.PurchaseSlip;
import model.bean.SaleDetail;
import model.bean.Stock;
import model.bean.StorageHistory;
import model.bean.Supplier;
import model.bean.Warehouse;
import model.dao.AuditLogsDAO;
import model.dao.PurchaseLinesDAO;
import model.dao.PurchasesDAO;
import model.dao.ReplenishmentLinesDAO;
import model.dao.ReplenishmentsDAO;
import model.dao.StockMovementsDAO;
import model.dao.StocksDAO;
import model.dao.WarehousesDAO;
import service.AuditLogsService;
import service.ProductsService;
import service.PurchaseLinesService;
import service.PurchasesService;
import service.SaleLinesService;
import service.SalesService;
import service.StockMovementsService;
import service.StocksService;
import service.SuppliersService;
import view.Displayer;
import view.OriginalDisplayer;
import view.WarehousesService;

public class Controller {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
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
				String inpSupp = disp.entStrNotNull("仕入先 (ID/キーワード) (必須): _");
				Supplier supplier = suppliersSvc.supplier(inpSupp);
				int inpPcWarehouseId = disp.entIntNotNull("倉庫 (ID) (必須): _");
				Warehouse warehouse = warehousesSvc.warehouse(inpPcWarehouseId);
				String inpPcJan = oDisp.entJanNotNull("JAN (必須): _");
				Product pcProduct = productsSvc.jan(inpPcJan);
				int pcQty = disp.entIntNotNull("数量 (整数>0) (必須): _");
				PurchaseSlip purchasesSlip = new PurchaseSlip(supplier.getId(), supplier.getName(), warehouse.getName(),
						inpPcJan, pcProduct.getName(), pcQty);
				disp.dispMsg("##受入確認##\n" + purchasesSlip);
				if (disp.confirm("この内容で実行しますか？ (Y/N): _")) {
					purchasesSlip.setStatu("RECEIVED");//purchase received
					int pcInsertId = purchasesSvc.insertSlip(purchasesSlip);
					PurchaseDetail pcDetail = new PurchaseDetail(pcInsertId, supplier.getId(), pcProduct.getId());//parchaselines
					purchaselinesSvc.insertDetail(pcDetail);
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
				int inpSlWarehouseId = disp.entIntNotNull("倉庫 (ID) (必須): _");
				Warehouse slWarehouse = warehousesSvc.warehouse(inpSlWarehouseId);
				String inpSlJan = oDisp.entJanNotNull("JAN (必須): _");
				Product slProduct = productsSvc.jan(inpSlJan);
				int slQty = disp.entIntNotNull("数量 (整数>0) (必須): _");
				int slPrice = disp.entInt("売価 [既定={std_price}]: _（未入力で既定採用）");
				SaleDetail slDetail = new SaleDetail(slProduct.getId(), slWarehouse.getName(), slProduct.getJan(),
						slQty, slPrice);
				disp.dispMsg("##出庫確認##" + slDetail);
				if (disp.confirm("この内容で実行しますか？ (Y/N): _")) {
					int slInsertId = salesSvc.insertSlip(slDetail);
					slDetail.setSaleId(slInsertId);
					stocksSvc.updateQty(slProduct, warehouse, slQty);//stock
					StorageHistory storHistory = new StorageHistory(pcProduct.getId(), warehouse.getId(), pcQty,
							"PURCHASE", "PURCHASE", slInsertId);//stockmovements
					int movementId = stockmovementsSvc.insertHistory(storHistory);
					AuditLog saleAL = new AuditLog("RECIEVE", "sals", slInsertId);//auditlogs
					auditlogsSvc.insertLog(saleAL);
					disp.dispMsg("OK: 出庫しました (sale_id={id}, movement_id={id}, 新在庫={qty})");
				} else {
					disp.dispMsg("やり直してください。");
				}
				break;
			case "5":
				disp.dispMsg("== 5. 発注起票（ORDERED） ==");
				String inpSupkey = disp.entStrNotNull("仕入先 (ID/キーワード) (必須): _");
				int supId = suppliersSvc.srchId(inpSupkey);
				if (supId <= 0) {
					disp.dispMsg("仕入先が見つかりません。処理を終了します。");
					break;
				}

				List<PurchaseDetail> pdList = new ArrayList<>();
				String inpDtlJan;
				int warehouseId = disp.entIntNotNull("倉庫ID (必須): _"); // 仮に倉庫を選択

				// 明細入力ループ
				do {
					inpDtlJan = disp.entStr("JAN [空で終了]: _");
					if (inpDtlJan == null || inpDtlJan.isEmpty())
						break;

					String resuJan = productsSvc.srchJan(inpDtlJan);
					if (resuJan == null) {
						disp.dispMsg("登録されているJANを入力してください。");
						continue;
					}

					int dtlQty = disp.entIntNotNull("数量 (整数>0) (必須): _");
					PurchaseDetail pd = new PurchaseDetail(resuJan, dtlQty);
					pdList.add(pd);

					disp.dispMsg("明細追加: " + resuJan + " x " + dtlQty);
					disp.dispMsg("##一覧表示 ##");
					pdList.forEach(System.out::println);

				} while (true);

				if (pdList.isEmpty()) {
					disp.dispMsg("明細が登録されていません。処理を終了します。");
					break;
				}

				if (disp.confirm("\nこの内容で発注を作成しますか？ (y/N): ")) {
					// 1. purchasesヘッダ作成
					PurchasesDAO purchaseDao = new PurchasesDAO();
					int purchaseId = purchaseDao.insertPurchase(supId, "ORDERED");

					// 2. purchase_lines追加
					PurchaseLinesDAO pldao = new PurchaseLinesDAO();
					StocksDAO stockDao = new StocksDAO();
					StockMovementsDAO smDao = new StockMovementsDAO();
					AuditLogsDAO auditDao = new AuditLogsDAO();

					for (PurchaseDetail pd : pdList) {
						pldao.insertLine(purchaseId, pd.getProductId(), pd.getQty());

						// 3. 在庫更新（stocks）
						stockDao.addStock(pd.getProductId(), warehouseId, pd.getQty());

						// 4. 在庫変遷（stock_movements）
						smDao.insertMovement(pd.getProductId(), warehouseId, pd.getQty(),
								"PURCHASE", "PURCHASE", purchaseId);

						// 5. 監査ログ（audit_logs）
						auditDao.insertLog("RECEIVE", "Purchase Order created", purchaseId, pd.getProductId(),
								pd.getQty());
					}

					disp.dispMsg(
							"OK: 発注を作成しました (purchase_id=" + purchaseId + ", 明細=" + pdList.size() + ") [状態: ORDERED]");
				} else {
					disp.dispMsg("発注作成をキャンセルしました。");
				}
				break;
			case "6":
				System.out.println("発注ID (必須): _　　　　　　　　（存在しない→ERROR: 対象が見つかりません）");
				int orderId = scan.nextInt();
				List<PurchaseSlip> pslist = pldao.srchRecordById(orderId);
				System.out.println("明細一覧表示（ordered / received / remain）");
				pslist.stream().get().forEach(System.out::println);
				System.out.println("受入倉庫 (ID) (必須): _");
				int inpReceiveId = scan.nextInt();
				WarehousesDAO wdao = new WarehousesDAO();
				int receiveId = wdao.srchWrhouseById(inpReceivedId);
				if (receivedId == 0) {
					System.out.println("登録している倉庫のIDを入力してください。");
				}
				System.out.println("受入数量入力（行ごと）");
				for (int i = 0; i < pslist.size(); i++) {
					System.out.println("#1：受入数量 [0..{remain}] : _");
					int inpReceiveQty = scan.nextInt();
					if (inpReceiveQty > pslist.get(i).getQty()) {
						System.out.println(" ERROR: 受入数量が発注残を超えています");
					}
					System.out.println("##一覧表示 ##");
					System.out.println(pslist.get(i).getName() + "数量：" + inpReceiveQty);
					System.out.println("この内容で検収しますか？ (y/N):");
					String inpCon = scan.nextLine();
					if (inpCon.equals("y")) {
						pdao.changeStatu(pslist.get(i).getId());
						pldao.reciept(pslist.get(i).getId());
						System.out.println("OK: 検収を登録しました（発注残: {remain_sum}）[状態: ORDERED]");
					}
				}
				System.out.println("OK: 検収を完了しました [状態: RECEIVED]");
				break;
			case "7":
				System.out.println("== 7. 発注案 作成（DRAFT）==");
				System.out.println("作成条件を入力してください");
				System.out.print("対象倉庫 [全部]:");
				String inputware = scan.nextLine();
				int wareId;
				WarehousesDAO whdao = new WarehousesDAO();
				if (inputware.matches("^\\d+$")) {
					int intware = Integer.parseInt(inputware);
					wareId = whdao.srchIdById(intware);
				} else {
					wareId = whdao.srchIdByWrd(inputware);
				}
				System.out.println("需要予測日数 [既定=0]");
				int numOfDays = scan.nextInt();
				System.out.println("##一覧表示 ##  (対象商品数/生成予定行数)");
				System.out.println();
				System.out.println("この内容で作成しますか？ (y/N): _");
				String inpJ = scan.nextLine();
				if (inpJ.equals("y")) {
					System.out.println("OK: 発注案を作成しました (id={repl_id}, 明細={n}) [状態: DRAFT]");
				}
				break;
			case "8":
				System.out.println("== 8. 発注案 承認 → 発注確定（ORDERED）==");
				System.out.println("発注案ID (必須): _");
				int inpRepId = scan.nextInt();
				ReplenishmentsDAO rpdao = new ReplenishmentsDAO();
				int repId = rpdao.srchIdById(inpRedId);
				ReplenishmentLinesDAO rpldao = new ReplenishmentlinesDAO();
				ReplenishDetail repDId = rpldao.srchById(inpRedId);
				System.out.println("明細一覧表示（suggested / approved(初期値はsuggested)");
				System.out.println(repDId);
				System.out.println("承認数量 [既定={suggested}] [Enter=既定]: _");
				int approvedQty = scan.nextInt();
				System.out.println("承認して発注を作成しますか？ (y/N): _");
				String approvedInp = scan.nextLine();
				if (approvedInp.equals("y")) {
					rpdao.changeStatu(repId);
					rpdao.approved(repId);
					rpldao.approved(rpldao, approvedQty);
					System.out.println("OK: 承認しました (purchase_id={id}, 明細={n}) [発注: ORDERED / 案: APPROVED]");
				}
				break;
			case "9":
				String num = disp.entStrNotNull("1) 登録  2) 更新    選択: _");
				if (num.equals("1")) {
					disp.dispMsg("== 9. 商品マスタ 登録 ==");
					String injan = disp.entStrNotNull("JAN (必須): _");
					String result = productsSvc.nameByjan(injan);
					if (result != null) {
						disp.dispMsg("同一JANの商品が既に存在します）");
					}
					String shohinmei = disp.entStrNotNull("商品名 (必須): ");
					int genka = disp.entIntNotNull("標準原価 (>=0) (必須): _");
					int baika = disp.entIntNotNull("標準売価 (>=0) (必須): _");
					int hacchu = disp.entIntNotNull("発注点 (>=0) (必須):");
					int rotto = disp.entIntNotNull("発注ロット (>=1) (必須): _");
					Product insProduct = new Product(injan, shohinmei, genka, baika, hacchu, rotto);
					disp.dispMsg("##一覧表示 ##\n" + insProduct);
					if (disp.confirm("この内容で登録しますか？ (y/N): _")) {
						int insPId = productsSvc.insertProduct(insProduct);
						disp.dispMsg("OK: 登録しました。" + insPId);
					} else {
						disp.dispMsg("やり直してください。");
					}
				} else if (num.equals("2")) {
					disp.dispMsg("== 9. 商品マスタ 更新 ==");
					String janorkey = disp.entStrNotNull("検索（JAN/キーワード→選択）→ 各値を [既定=現値] で上書き");
					int upProId = productsSvc.srchId(janorkey);
					String shohinmei = disp.entStrNotNull("商品名 (必須): ");
					int genka = disp.entIntNotNull("標準原価 (>=0) (必須): _");
					int baika = disp.entIntNotNull("標準売価 (>=0) (必須): _");
					int hacchu = disp.entIntNotNull("発注点 (>=0) (必須):");
					int rotto = disp.entIntNotNull("発注ロット (>=1) (必須): _");
					Product updateP = new Product(shohinmei, genka, baika, hacchu, rotto);
					disp.dispMsg("##一覧表示 ##\n" + updateP);
					if (disp.confirm("この内容で更新しますか？ (y/N): _")) {
						int upResultId = productsSvc.upProduct(updateP, upProId);
						System.out.println("OK: 更新しました。id= " + upResultId);
					} else {
						disp.dispMsg("やり直してください。");
					}
				}
				break;
			case "10":
				String num2 = disp.entStrNotNull("1) 登録  2) 更新    選択: _");
				if (num2.equals("1")) {
					disp.dispMsg("== 10. 仕入先マスタ 登録 ==");
					String supname = disp.entStrNotNull("名称 (必須):");
					int rtime = disp.entIntNotNull("リードタイム(日)(必須): _");
					String phone = disp.entStrNotNull("電話 (必須): _");
					String mail = disp.entStrNotNull("Email (必須): _");
					Supplier sup = new Supplier(supname, rtime, phone, mail);
					disp.dispMsg("##一覧表示 ##\n" + sup);
					if (disp.confirm("この内容で登録しますか？ (y/N): _")) {
						int insrResult = suppliersSvc.insrtSup(sup);
						disp.dispMsg("OK: 登録しました id=" + insrResult);
					} else {
						disp.dispMsg("やり直してください。");
					}
				} else if (num2.equals("2")) {
					disp.dispMsg("== 10. 仕入先マスタ 更新 ==");
					String inpUpdtSupKey = disp.entStrNotNull("検索（ID/キーワード→選択）→ 各値を [既定=現値] で上書き");
					int resultId = suppliersSvc.srchId(inpUpdtSupKey);
					disp.dispMsg("== 10. 仕入先マスタ 登録 ==");
					String supname = disp.entStrNotNull("名称 (必須):");
					int rtime = disp.entIntNotNull("リードタイム(日)(必須): _");
					String phone = disp.entStrNotNull("電話 (必須): _");
					String mail = disp.entStrNotNull("Email (必須): _");
					Supplier sup = new Supplier(supname, rtime, phone, mail);
					disp.dispMsg("##一覧表示 ##\n" + sup);
					if (disp.confirm("この内容で更新しますか？ (y/N): _")) {
						int updtResult = suppliersSvc.updtSup(sup, resultId);
						disp.dispMsg("OK: 更新しました id=" + updtResult);
					} else {
						disp.dispMsg("やり直してください。");
					}
				}
				break;
			case "11":
				disp.dispMsg("== 11. レポートCSV出力 ==");
				String reportType = disp.entStrNotNull("レポート種別 [sales-ranking|stockout|stock-turnover] (必須): _");
				String from = disp.entStrNotNull("from (YYYY-MM-DD) (必須): _ ");
				String to = disp.entStr("to (YYYY-MM-DD) (必須): _（種別により不要な場合はスキップ）");
				System.out.println("OK: 出力しました ./exports/{filename}.csv");
				break;
			case "0":
				disp.dispMsg("終了します。");
				break;
			default:
				disp.dispMsg("０～11の数字を入力してください。");
			}
		}
	}
}