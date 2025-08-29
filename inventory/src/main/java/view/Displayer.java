package view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Displayer {
	Scanner scan = new Scanner(System.in);

	public String dispMenuAndSelect() {//menuを表示
		dispMsg("""
				== 在庫管理 CLI ==
				1) 在庫参照
				2) 入出庫履歴
				3) クイック入荷
				4) クイック販売
				5) 発注起票（ORDERED）
				6) 検収（発注ID指定）
				7) 発注案 作成（DRAFT）
				8) 発注案 承認→発注確定
				9) 商品マスタ 登録/更新
				10) 仕入先マスタ 登録/更新
				11) レポートCSV出力
				0) 終了
				選択: _
									""");
		String select = scan.nextLine();
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

	public void dispMsg(String msg) {//msgを表示
		System.out.println(msg);
	}

	public String entStrNotNull(String msg) {//msgを表示&nullだめ
		while (true) {
			String str = entStr(msg);
			if (str == null) {
				System.out.println("入力必須項目です。");
			} else {
				return str;
			}
		}
	}

	public String entStr(String msg) {//msgを表示&nullOK
		dispMsg(msg);
		return scan.nextLine();
	}

	public int entIntNotNull(String string) {
		while (true) {
			int inte = entInt(string);
			if (inte != 0) {
				return inte;
			}

		}
	}

	public int entInt(String msg) {
		while (true) {
			dispMsg(msg);
			try {
				int inte = scan.nextInt();
				scan.nextLine();
				return inte;
			} catch (InputMismatchException e) {
				dispMsg("数値を入力してください。");
			}
		}
	}

	public boolean confirm(String string) {
		while (true) {
			String inp = entStrNotNull(string);
			if (inp.matches("(Y|y)")) {
				return true;
			} else if (inp.matches("(N|n")) {
				return false;
			}else {
				dispMsg("「Y」または「N」を入力してください。");
			}
		}
	}
=======
import java.util.Scanner;

public class Displayer {
	Scanner scan = new Scanner(System.in);

	public String dispMenuAndSelect() {//menuを表示
		dispMsg("""
				== 在庫管理 CLI ==
				1) 在庫参照
				2) 入出庫履歴
				3) クイック入荷
				4) クイック販売
				5) 発注起票（ORDERED）
				6) 検収（発注ID指定）
				7) 発注案 作成（DRAFT）
				8) 発注案 承認→発注確定
				9) 商品マスタ 登録/更新
				10) 仕入先マスタ 登録/更新
				11) レポートCSV出力
				0) 終了
				選択: _
									""");
		String select = scan.nextLine();
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

	public void dispMsg(String msg) {//msgを表示
		System.out.println(msg);
	}

	public String entStrNotNull(String msg) {//msgを表示&nullだめ
		while (true) {
			String str = entStr(msg);
			if (str == null) {
				System.out.println("入力必須項目です。");
			} else {
				return str;
			}
		}
	}

	public String entStr(String msg) {//msgを表示&nullOK
		dispMsg(msg);
		return scan.nextLine();
	}

	

}
