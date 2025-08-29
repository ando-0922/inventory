package controller;

import java.time.LocalDate;
import java.util.List;

import model.bean.Stock;
import model.bean.StorageHistory;
import service.StockMovementsService;
import service.StocksService;
import view.Displayer;
import view.MenuDisplayer;
import view.OriginalDisplayer;

public class StocksController{

	public void select() {
		//printf  
		//% 書式指定子開始
		//d 整数10進数　s文字列　c文字
		//値:最小桁数指定　.値:文字列の最大幅を指定
		//-:左詰め　0:0埋める
		StocksService stocksSvc = new StocksService();
		StockMovementsService stockmovementsSvc = new StockMovementsService();
		Displayer disp = new Displayer();
		OriginalDisplayer oDisp = new OriginalDisplayer();
		MenuDisplayer menu = new MenuDisplayer();
		
		while (true) {
			String select = menu.dispStocksMenu();
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
			default:
				disp.dispMsg("前に戻ります。");
				return;
			}
		}
	}
	
}