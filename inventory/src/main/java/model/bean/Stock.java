package model.bean;

public class Stock {
	private String name;
	private int warehouseId;
	private String warehouse;
	private int qty;
	private int productId;

	Stock(int p, int w, int q) {
		this.setProductId(p);
		this.warehouseId = w;
		this.qty = q;

	}

	public Stock(String name, int warehouseId, String warehouse, int qty) {
		super();
		this.name = name;
		this.warehouseId = warehouseId;
		this.warehouse = warehouse;
		this.qty = qty;
	}

	public Stock(String name2, String warehouse2, int qty2) {
		this.name = name2;
		this.warehouse = warehouse2;
		this.qty = qty2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	@Override
	public String toString() {
		return "商品名：" + name + "\t" + warehouseId + "：" + warehouse + "\t在庫数：" + qty;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	
	
}
