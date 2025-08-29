package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import model.bean.AuditLog;
import model.bean.Product;
import model.bean.Purchase;
import model.bean.PurchaseLine;
import model.bean.Replenishment;
import model.bean.ReplenishmentLine;
import model.bean.Sale;
import model.bean.SaleLine;
import model.bean.Stock;
import model.bean.StockMovement;
import model.bean.Supplier;
import model.bean.Warehouse;
import service.AuditLogsService;
import service.ProductsService;
import service.PurchaseLinesService;
import service.PurchasesService;
import service.SalesService;
import service.StockMovementsService;
import service.StocksService;
import service.SuppliersService;
import service.WarehousesService;
import view.Displayer;
import view.OriginalDisplayer;

public class G {
		//printf  
		//% 書式指定子開始
		//d 整数10進数　s文字列　c文字
		//値:最小桁数指定　.値:文字列の最大幅を指定
		//-:左詰め　0:0埋める
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
			if (s.getProductId() == productId && (warehouseFilter == null || s.warehouseId == warehouseFilter))
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
}