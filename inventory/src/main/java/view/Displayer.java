package view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Displayer {
	Scanner scan = new Scanner(System.in);

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
			} else {
				dispMsg("「Y」または「N」を入力してください。");
			}
		}
	}
	public void dispFormat(String format, Object obj) {
		System.out.printf(format,obj);
	}

}
