package view;

import java.util.Scanner;

public class MenuDisplayer extends Displayer {
	Scanner scan = new Scanner(System.in);

	public String dispMenuAndSelect() {//menuを表示
		dispMsg("""
				== 在庫管理 CLI ==
				1) 在庫管理
				2) 入荷処理
				3) 販売処理
				4) 発注処理
				5) 商品管理
				6) 仕入先管理
				0) 終了
				選択: _
									""");
		String select = scan.nextLine();
		return select;
	}

	public String dispStocksMenu() {//在庫管理のmenuを表示
		String select = entStrNotNull("");
		dispMsg(switch (select) {
		case "1" -> "== 1. 在庫参照 ==";
		case "2" -> "== 2. 入出庫履歴 ==";
		default -> "";
		});
		return select;
	}
	public String dispPurchasesMenu() {//入荷処理のmenuを表示
		String select = entStrNotNull("");
		dispMsg(switch (select) {
		case "1" -> "== 1. クイック入荷 ==";
		default -> "";
		});
		return select;
	}
	public String dispSalesMenu() {//販売処理のmenuを表示
		String select = entStrNotNull("");
		dispMsg(switch (select) {
		case "1" -> "== 1. クイック販売 ==";
		default -> "";
		});
		return select;
	}
	public String dispOrdersMenu() {//発注処理のmenuを表示
		String select = entStrNotNull("");
		dispMsg(switch (select) {
		case "1" -> "== 1. 発注起票（ORDERED） ==";
		case "2" -> "== 2. 検収（発注ID指定） ==";
		case "3" -> "== 3. 発注案 作成（DRAFT）==";
		case "4" -> "== 4. 発注案 承認 → 発注確定（ORDERED）==";
		default -> "";
		});
		return select;
	}
	public String dispProductsMenu() {//商品管理のmenuを表示
		String select = entStrNotNull("");
		dispMsg(switch (select) {
		case "1" -> "== 1. 商品の登録 ==";
		case "2" -> "== 2. 商品の更新 ==";
		default -> "";
		});
		return select;
	}
	public String dispSuppliersMenu() {//仕入先管理のmenuを表示
		String select = entStrNotNull("");
		dispMsg(switch (select) {
		case "1" -> "== 1. 仕入先の登録 ==";
		case "2" -> "== 2. 仕入先の更新 ==";
		default -> "";
		});
		return select;
	}
	public void dispMenuOnCase(String select) {//caseごとのmenuを表示
		System.out.println(switch (select) {
		case "1" -> "== 1. 在庫参照 ==";
		case "2" -> "== 2. 入出庫履歴 ==";
		case "3" -> "== 3. クイック入荷 ==";
		case "4" -> "== 4. クイック販売 ==";
		case "5" -> "== 5. 発注起票（ORDERED） ==";
		case "6" -> "== 6. 検収（発注ID指定） ==";
		case "7" -> "== 7. 発注案 作成（DRAFT）==";
		case "8" -> "== 8. 発注案 承認 → 発注確定（ORDERED）==";
		case "9" -> "== 9. 商品マスタ 登録/更新 == ";
		case "10" -> "== 10. 仕入先マスタ 登録/更新 == ";
		case "11" -> "== 11. レポートCSV出力 == ";
		default -> "";
		});
	}

}
