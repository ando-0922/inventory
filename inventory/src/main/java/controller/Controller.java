package controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import model.bean.Product;
import model.bean.PurchaseDetail;
import model.bean.PurchaseSlip;
import model.bean.Stock;
import model.bean.StorageHistory;
import model.bean.Supplier;
import model.bean.Warehouse;
import model.dao.ProductsDAO;
import model.dao.PurchaseLinesDAO;
import model.dao.PurchasesDAO;
import model.dao.StockDAO;
import model.dao.StockMovementsDAO;
import model.dao.SuppliersDAO;
import model.dao.WarehousesDAO;
import service.Service;
import view.Displayer;
import view.OriginalDisplayer;

public class Controller {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Service service = new Service();
		Displayer disp = new Displayer();
		OriginalDisplayer oDisp = new OriginalDisplayer();
		while (true) {
			String select = disp.dispMenuAndSelect();
			disp.dispMenuOnCase(select);
			switch (select) {
			case "1":
				String inpPickWarehouse = oDisp.srchWordWarehouse();//検索倉庫を入力
				List<Stock> stockList = service.stockList(inpPickWarehouse);
				String inpPartOfName = disp.entStr("キーワード [空=全件] : ");
				oDisp.stockListbrunchOnNull(stockList, inpPartOfName);//在庫の一覧表示
				break;
			case "2":
				String inpWarehouseHis = oDisp.srchWordWarehouse();//検索倉庫を入力
				List<StorageHistory> historyList = service.historyList(inpWarehouseHis);//指定した倉庫の履歴の検索結果
				String hisType = oDisp.entHisType();//区別を入力
				LocalDate period[] = oDisp.entPeriod();//指定する期間を入力
				oDisp.dispHistory(historyList, hisType, period);//履歴を表示
				break;
			case "3":
				System.out.print("仕入先 (ID/キーワード) (必須): _");
				String shire = scan.nextLine();
				PurchaseSlip ps = new PurchaseSlip();
				SuppliersDAO spdao = new SuppliersDAO();
				Supplier sp = new Supplier();
				WarehousesDAO waredao = new WarehousesDAO();
				Warehouse wh = new Warehouse();
				ProductsDAO pddao = new ProductsDAO();
				Product pd = new Product();
				if (shire.matches("^\\d+$")) {
					sp = spdao.srchIdNameById(Integer.parseInt(shire));
				} else {
					sp = spdao.srchIdNameByWrd(shire);
				}
				
				System.out.print("倉庫 (ID) (必須): _");
				int soko = scan.nextInt();
				scan.nextLine();
				wh = waredao.srchById(soko);
				
				System.out.print("JAN (必須): _");
				String jan = scan.nextLine();
				pd = pddao.srchJan(jan);
				if (pd.getJan() == null) {
					System.out.println("正しいJANを入力してください。");
				}
				
				System.out.print("数量 (整数>0) (必須): _");
				int quantity = scan.nextInt();
				scan.nextLine();
				if (quantity <= 0) {
					System.out.println("数量は１以上を入力してください。");
				} else {
					ps.setQty(quantity);
				}

				System.out.println("##受入確認##");
				System.out.print(ps);
				System.out.print("この内容で実行しますか？ (y/N): _");
				String yOrN = scan.nextLine();
				if (yOrN.equals("y")) {
					//purchase received
					ps.setStatu("RECEIVED");
					PurchasesDAO pcdao = new PurchasesDAO();
					pcdao.insert(ps);
					//parchaselines
					PurchaseLinesDAO pldao = new PurchaseLinesDAO();
					PurchaseDetail pd = new PurchaseDetail();
					pldao.insert(pd);
					//stock
					StockDAO sdao = new StockDAO();
					int warehouseid = waredao.srchByWrd(ps.getWarehouse());
					sdao.upQty(pd, ps);
					//stockmovements
					StockMovementsDAO smodao = new StockMovementsDAO();
					smodao.changeType(pd, warehouseid);
					//auditlogs
					System.out.println("OK: 受入を登録しました (purchase_id={id}, movement_id={id}, 新在庫={qty})");
				}
				break;
			case "4":
				System.out.print("倉庫 (ID) (必須): _");
				String souko = scan.nextLine();
				System.out.print("JAN (必須): _（未登録→エラー） → 現在庫を表示：現在庫: {stock}");
				String jan2 = scan.nextLine();
				System.out.print("数量 (整数>0) (必須): _");
				String q = scan.nextLine();
				System.out.print("売価 [既定={std_price}]: _（未入力で既定採用）");
				String qp = scan.nextLine();
				System.out.println("""
						##出庫確認##
						倉庫: {warehouse} / 商品: {jan} {name}
						数量: {qty} / 単価: {price} / 金額: {qty*price}
						この内容で実行しますか？ (y/N): _

						OK: 出庫しました (sale_id={id}, movement_id={id}, 新在庫={qty})
												""");
				break;
			//			case "5":
			//				System.out.println("== 5. 発注起票（ORDERED） ==");
			//				System.out.println("仕入先 (ID/キーワード) (必須): _");
			//				String supkey = scan.nextLine();
			//				PurchasesDAO pdao = new PurchasesDAO();
			//				int id;
			//				if (supkey.matches("^\\d+$")) {
			//					id = pdao.srchId(supkey);
			//				} else {
			//					id = pdao.srchIdByWrd(supkey);
			//				}
			//				/////明細入力ループ///
			//				String dtlJan;
			//				do {
			//					System.out.println("JAN [空で終了]: _（空Enterで終了）");
			//					dtlJan = scan.nextLine();
			//					String resuJan = pdao.srchJan(dtlJan);
			//					if (resuJan == null) {
			//						System.out.println("登録されているJANを入力してください。");
			//					}
			//					System.out.println("数量 (整数>0) (必須): _");
			//					int dtlQty = scan.nextInt();
			//					System.out.println("##一覧表示 ##\nこの内容で発注を作成しますか？ (y/N): ");
			//					PurchaseDetail newPd = new PurchaseDetail(resuJan, dtlQty);
			//					String createC = scan.nextLine();
			//					if (createC.equals("y")) {
			//						PurchaseLinesDAO pldao = new PurchaseLinesDAO();
			//						pldao.insertP(newPd);
			//						System.out.println("OK: 発注を作成しました (purchase_id={id}, 明細={n}) [状態: ORDERED]");
			//					}
			//				} while (dtlJan != null);
			//				break;
			//			case "6":
			//				System.out.println("== 6. 検収（発注ID指定） ==");
			//				System.out.println("発注ID (必須): _　　　　　　　　（存在しない→ERROR: 対象が見つかりません）");
			//				int orderId = scan.nextInt();
			//				List<PurchaseSlip> pslist = pldao.srchRecordById(orderId);
			//				System.out.println("明細一覧表示（ordered / received / remain）");
			//				pslist.stream().get().forEach(System.out::println);
			//				System.out.println("受入倉庫 (ID) (必須): _");
			//				int inpReceiveId = scan.nextInt();
			//				WarehousesDAO wdao = new WarehousesDAO();
			//				int receiveId = wdao.srchWrhouseById(inpReceivedId);
			//				if (receivedId == 0) {
			//					System.out.println("登録している倉庫のIDを入力してください。");
			//				}
			//				System.out.println("受入数量入力（行ごと）");
			//				for (int i = 0; i < pslist.size(); i++) {
			//					System.out.println("#1：受入数量 [0..{remain}] : _");
			//					int inpReceiveQty = scan.nextInt();
			//					if (inpReceiveQty > pslist.get(i).getQty()) {
			//						System.out.println(" ERROR: 受入数量が発注残を超えています");
			//					}
			//					System.out.println("##一覧表示 ##");
			//					System.out.println(pslist.get(i).getName() + "数量：" + inpReceiveQty);
			//					System.out.println("この内容で検収しますか？ (y/N):");
			//					String inpCon = scan.nextLine();
			//					if (inpCon.equals("y")) {
			//						pdao.changeStatu(pslist.get(i).getId());
			//						pldao.reciept(pslist.get(i).getId());
			//						System.out.println("OK: 検収を登録しました（発注残: {remain_sum}）[状態: ORDERED]");
			//					}
			//				}
			//				System.out.println("OK: 検収を完了しました [状態: RECEIVED]");
			//				break;
			//			case "7":
			//				System.out.println("== 7. 発注案 作成（DRAFT）==");
			//				System.out.println("作成条件を入力してください");
			//				System.out.print("対象倉庫 [全部]:");
			//				String inputware = scan.nextLine();
			//				int wareId;
			//				WarehousesDAO whdao = new WarehousesDAO();
			//				if (inputware.matches("^\\d+$")) {
			//					int intware = Integer.parseInt(inputware);
			//					wareId = whdao.srchIdById(intware);
			//				} else {
			//					wareId = whdao.srchIdByWrd(inputware);
			//				}
			//				System.out.println("需要予測日数 [既定=0]");
			//				int numOfDays = scan.nextInt();
			//				System.out.println("##一覧表示 ##  (対象商品数/生成予定行数)");
			//				System.out.println();
			//				System.out.println("この内容で作成しますか？ (y/N): _");
			//				String inpJ = scan.nextLine();
			//				if (inpJ.equals("y")) {
			//					System.out.println("OK: 発注案を作成しました (id={repl_id}, 明細={n}) [状態: DRAFT]");
			//				}
			//				break;
			//			case "8":
			//				System.out.println("== 8. 発注案 承認 → 発注確定（ORDERED）==");
			//				System.out.println("発注案ID (必須): _");
			//				int inpRepId = scan.nextInt();
			//				ReplenishmentsDAO rpdao = new ReplenishmentsDAO();
			//				int repId = rpdao.srchIdById(inpRedId);
			//				ReplenishmentLinesDAO rpldao = new ReplenishmentlinesDAO();
			//				ReplenishDetail repDId = rpldao.srchById(inpRedId);
			//				System.out.println("明細一覧表示（suggested / approved(初期値はsuggested)");
			//				System.out.println(repDId);
			//				System.out.println("承認数量 [既定={suggested}] [Enter=既定]: _");
			//				int approvedQty = scan.nextInt();
			//				System.out.println("承認して発注を作成しますか？ (y/N): _");
			//				String approvedInp = scan.nextLine();
			//				if (approvedInp.equals("y")) {
			//					rpdao.changeStatu(repId);
			//					rpdao.approved(repId);
			//					rpldao.approved(rpldao, approvedQty);
			//					System.out.println("OK: 承認しました (purchase_id={id}, 明細={n}) [発注: ORDERED / 案: APPROVED]");
			//				}
			//				break;
			//			case "9":
			//				System.out.println("== 9. 商品マスタ 登録/更新 ==");
			//				System.out.print("1) 登録  2) 更新    選択: _");
			//				String num = scan.nextLine();
			//				ProductsDAO pdao = new ProductsDAO();
			//				if (num.equals("1")) {
			//					System.out.println("== 9. 商品マスタ 登録 ==");
			//					System.out.print("JAN (必須): _");
			//					String injan = scan.nextLine();
			//					String result = pdao.srchNameByJan(injan);
			//					if (result != null) {
			//						System.out.println("同一JANの商品が既に存在します）");
			//					}
			//					System.out.print("商品名 (必須): ");
			//					String shohinmei = scan.nextLine();
			//					System.out.print("標準原価 (>=0) (必須): _");
			//					int genka = scan.nextInt();
			//					scan.nextLine();
			//					System.out.print("標準売価 (>=0) (必須): _");
			//					int baika = scan.nextInt();
			//					scan.nextLine();
			//					System.out.print("発注点 (>=0) (必須):");
			//					int hacchu = scan.nextInt();
			//					scan.nextLine();
			//					System.out.println("発注ロット (>=1) (必須): _");
			//					int rotto = scan.nextInt();
			//					scan.nextLine();
			//					Product rp = new Product(injan, shohinmei, genka, baika, hacchu, rotto);
			//					System.out.println("##一覧表示 ##\n" + rp);
			//					System.out.println("この内容で登録しますか？ (y/N): _");
			//					String yorno = scan.nextLine();
			//					if (yorno.equals("y")) {
			//						pdao.insertP(rp);
			//						System.out.println("OK: 登録しました (id={id})");
			//					}
			//				} else if (num.equals("2")) {
			//					System.out.println("== 9. 商品マスタ 更新 ==");
			//					System.out.println("検索（JAN/キーワード→選択）→ 各値を [既定=現値] で上書き");
			//					String janorkey = scan.nextLine();
			//					int upId;
			//					if (janorkey.matches("^\\d+$")) {
			//						upId = pdao.srchIdByJan(janorkey);
			//					} else {
			//						upId = pdao.srchIdByKey(janorkey);
			//					}
			//					System.out.print("商品名 (必須): ");
			//					String shohinmei = scan.nextLine();
			//					System.out.print("標準原価 (>=0) (必須): _");
			//					int genka = scan.nextInt();
			//					scan.nextLine();
			//					System.out.print("標準売価 (>=0) (必須): _");
			//					int baika = scan.nextInt();
			//					scan.nextLine();
			//					System.out.print("発注点 (>=0) (必須):");
			//					int hacchu = scan.nextInt();
			//					scan.nextLine();
			//					System.out.println("発注ロット (>=1) (必須): _");
			//					int rotto = scan.nextInt();
			//					scan.nextLine();
			//					Product p = new Product(shohinmei, genka, baika, hacchu, rotto);
			//					System.out.println("##一覧表示 ##\n" + p);
			//					System.out.println("この内容で更新しますか？ (y/N): _");
			//					String yorno = scan.nextLine();
			//					if (yorno.equals("y")) {
			//						pdao.updateP(p, upId);
			//						System.out.println("OK: 更新しました (id={id})");
			//					}
			//				}
			//				break;
			//			case "10":
			//				System.out.println("== 10. 仕入先マスタ 登録/更新 ==");
			//				System.out.print("1) 登録  2) 更新    選択: _");
			//				String num2 = scan.nextLine();
			//				if (num2.equals("1")) {
			//					System.out.println("== 10. 仕入先マスタ 登録 ==");
			//					System.out.println("名称 (必須):");
			//					String supname = scan.nextLine();
			//					System.out.println("リードタイム(日)(必須): _");
			//					String rtime = scan.nextLine();
			//					System.out.println("電話 (必須): _");
			//					String phone = scan.nextLine();
			//					System.out.println("Email (必須): _");
			//					String mail = scan.nextLine();
			//					System.out.println("##一覧表示 ##");
			//					System.out.println("この内容で登録しますか？ (y/N): _");
			//					String yesorno = scan.nextLine();
			//					Supplier sup = new Supplier(supname, rtime, phone, mail);
			//					if (yesorno.equals("y")) {
			//						spdao.insertS(sup);
			//						System.out.println("OK: 登録しました (id={id})");
			//					}
			//				} else if (num2.equals("2")) {
			//					System.out.println("== 10. 仕入先マスタ 更新 ==");
			//					System.out.println("検索（ID/キーワード→選択）→ 各値を [既定=現値] で上書き");
			//					String supkey = scan.nextLine();
			//					int resultId;
			//					if (supkey.matches("^\\d+$")) {
			//						int supId = Integer.parseInt(supkey);
			//						resultId = spdao.srchId(supId);
			//					} else {
			//						resultId = spdao.srchIdByKey(supkey);
			//					}
			//					System.out.println("== 10. 仕入先マスタ 登録 ==");
			//					System.out.println("名称 (必須):");
			//					String supname = scan.nextLine();
			//					System.out.println("リードタイム(日)(必須): _");
			//					String rtime = scan.nextLine();
			//					System.out.println("電話 (必須): _");
			//					String phone = scan.nextLine();
			//					System.out.println("Email (必須): _");
			//					String mail = scan.nextLine();
			//					Supplier sup = new Supplier(supname, rtime, phone, mail);
			//					System.out.println("##一覧表示 ##\n" + sup);
			//					System.out.println("この内容で更新しますか？ (y/N): _");
			//					String yorno = scan.nextLine();
			//					if (yorno.equals("y")) {
			//						spdao.updateS(sup, resultId);
			//						System.out.println("OK: 更新しました (id={id})");
			//					}
			//				}
			//				break;
			case "11":
				System.out.println("== 11. レポートCSV出力 ==");
				System.out.println("レポート種別 [sales-ranking|stockout|stock-turnover] (必須): _");
				System.out.println("from (YYYY-MM-DD) (必須): _ ");
				System.out.println("to (YYYY-MM-DD) (必須): _（種別により不要な場合はスキップ）");
				System.out.println("OK: 出力しました ./exports/{filename}.csv");
				break;
			case "0":
				System.out.println("終了します。");
				break;
			default:
				System.out.println("０～11の数字を入力してください。");
			}
		}
	}
}