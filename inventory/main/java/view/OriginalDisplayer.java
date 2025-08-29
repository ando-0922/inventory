package view;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.bean.Stock;
import model.bean.StorageHistory;

public class OriginalDisplayer extends Displayer {
	public void stockListbrunchOnNull(List<Stock> list, String inpPartOfName) {//case1の表示
		dispMsg("在庫の一覧");
		if (inpPartOfName != null) {
			list.stream().filter(name -> name.getName().contains(inpPartOfName)).forEach(System.out::println);
		} else {
			list.stream().forEach(System.out::println);
		}
	}

	public String srchWordWarehouse() {//case1,2
		while (true) {
			String str = entStrNotNull("倉庫を選択してください (ID) [全部]:_");
			if (!(str.equals("全部")) && !(str.matches("^\\d+$"))) {
				dispMsg("存在する倉庫ID、または「全部」と入力してください。");
			} else {
				return str;
			}
		}

	}

	public String entHisType() {//case2
		while (true) {
			String str = entStrNotNull("種別 [ALL|PURCHASE|SALE|ADJUST]: _");
			if (str.matches("[a-zA-Z]")) {
				return str.toUpperCase();
			} else {
				dispMsg("ALL|PURCHASE|SALE|ADJUST のいずれかを入力してください。");
			}
		}
	}

	public LocalDate[] entPeriod() {//case2
		String[] ary = new String[2];
		LocalDate period[] = new LocalDate[2];
		Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		outloop: while (true) {
			ary[0] = entStrNotNull("期間 From (YYYY-MM-DD) (必須): _");
			ary[1] = entStrNotNull("期間 To (YYYY-MM-DD) (必須): _");
			for (String s : ary) {
				Matcher m = p.matcher(s);
				if (!m.find()) {
					dispMsg("入力形式が間違っています。");
					continue outloop;
				}
			}
			period[0] = LocalDate.parse(ary[0]);
			period[1] = LocalDate.parse(ary[1]);
			if (period[0].isAfter(period[1])) {
				dispMsg("期間の指定が不正です。");
			} else {
				break;
			}
		}
		return period;
	}

	public void dispHistory(List<StorageHistory> list, String hisType, LocalDate[] period) {
		dispMsg("入出庫の一覧");
		list.stream().filter(type -> type.getType().equals(hisType))
				.filter(date -> date.getMovedAt().toLocalDateTime().toLocalDate().isBefore(period[0]))
				.filter(date -> date.getMovedAt().toLocalDateTime().toLocalDate().isAfter(period[1]))
				.forEach(System.out::println);
	}
}
