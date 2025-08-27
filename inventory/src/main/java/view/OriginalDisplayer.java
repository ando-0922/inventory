package view;

import java.util.List;

import model.bean.Stock;

public class OriginalDisplayer extends Displayer{
	public void stockListbrunchOnNull(List<Stock> list, String inpPartOfName) {//case1の表示
		dispMsg("在庫の一覧");
		if (inpPartOfName != null) {
			list.stream().filter(name -> name.getName().contains(inpPartOfName)).forEach(System.out::println);
		} else {
			list.stream().forEach(System.out::println);
		}
	}
	
	public String srchWordWarehouse() {
		String str = entStrNotNull("倉庫を選択してください (ID) [全部]:_");
		if(str)
			
			
			
			if (warehouse.equals("全部")) {
				list = stockdao.allStocks();
			} else if (warehouse.matches("^\\d+$")) {
				list = stockdao.stocksById(Integer.parseInt(warehouse));
			}
	}
}
