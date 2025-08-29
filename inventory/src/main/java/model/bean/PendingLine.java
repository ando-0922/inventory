package model.bean;

public class PendingLine {
	private int productId;
	private String jan;
	private String name;
	private int qty;
	public PendingLine(int productId, String jan, String name, int qty) {
		super();
		this.productId = productId;
		this.jan = jan;
		this.name = name;
		this.qty = qty;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getJan() {
		return jan;
	}
	public void setJan(String jan) {
		this.jan = jan;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
}
