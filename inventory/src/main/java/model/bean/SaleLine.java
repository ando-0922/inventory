package model.bean;

public class SaleLine {
	private int id;
	private int saleId;
	private int productId;
	private String warehouse;
	private String jan;
	private int qty;
	private int unitPrice;
	
	public SaleLine(int id, int saleId, int productId, String warehouse, String jan, int qty, int unitPrice) {
		super();
		this.id = id;
		this.saleId = saleId;
		this.productId = productId;
		this.warehouse = warehouse;
		this.jan = jan;
		this.qty = qty;
		this.unitPrice = unitPrice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSaleId() {
		return saleId;
	}

	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getJan() {
		return jan;
	}

	public void setJan(String jan) {
		this.jan = jan;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public String toString() {
		return "SaleLine [id=" + id + ", saleId=" + saleId + ", productId=" + productId + ", warehouse=" + warehouse
				+ ", jan=" + jan + ", qty=" + qty + ", unitPrice=" + unitPrice + "]";
	}
	
}
