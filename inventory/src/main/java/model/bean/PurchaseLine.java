package model.bean;

public class PurchaseLine {
	private int id;
	private int purchaseId;
	private int supplierId;
	private int productId;
	private int orderedQty;
	private int receivedQty;
	
	public PurchaseLine(int id, int purchaseId, int productId, int orderedQty, int receivedQty) {
		super();
		this.id = id;
		this.purchaseId = purchaseId;
		this.productId = productId;
		this.orderedQty = orderedQty;
		this.receivedQty = receivedQty;
	}
	public PurchaseLine() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getOrderedQty() {
		return orderedQty;
	}
	public void setOrderedQty(int orderedQty) {
		this.orderedQty = orderedQty;
	}
	public int getReceivedQty() {
		return receivedQty;
	}
	public void setReceivedQty(int receivedQty) {
		this.receivedQty = receivedQty;
	}
	@Override
	public String toString() {
		return "PurchaseDetail [id=" + id + ", purchaseId=" + purchaseId + ", productId=" + productId + ", orderedQty="
				+ orderedQty + ", receivedQty=" + receivedQty + "]";
	}
	public PurchaseLine(int productId, int qty) {
		this.productId = productId;
		this.orderedQty = qty;
	}
	public PurchaseLine(int purchaseId, int supplierId, int productId) {
		this.purchaseId = purchaseId;
		this.supplierId = supplierId;
		this.productId = productId;
	}
}
