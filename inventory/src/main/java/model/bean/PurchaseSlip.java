package model.bean;

import java.sql.Timestamp;

public class PurchaseSlip {
	private int id;
	private int supplierId;
	private String supplier;
	private String warehouse;
	private String jan;
	private String name;
	private int qty;
	private Timestamp orderedAt;
	private Timestamp receivedAt;
	private String statu;
	public PurchaseSlip(int id, int supplierId, String supplier, String warehouse, String jan, int qty,
			Timestamp orderedAt, Timestamp receivedAt, String statu) {
		super();
		this.id = id;
		this.supplierId = supplierId;
		this.supplier = supplier;
		this.warehouse = warehouse;
		this.jan = jan;
		this.qty = qty;
		this.orderedAt = orderedAt;
		this.receivedAt = receivedAt;
		this.statu = statu;
	}
	public PurchaseSlip() {
	
	}
	public PurchaseSlip(int id2, String name2) {
		this.id = id2;
		this.name = name2;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
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
	public Timestamp getOrderedAt() {
		return orderedAt;
	}
	public void setOrderedAt(Timestamp orderedAt) {
		this.orderedAt = orderedAt;
	}
	public Timestamp getReceivedAt() {
		return receivedAt;
	}
	public void setReceivedAt(Timestamp receivedAt) {
		this.receivedAt = receivedAt;
	}
	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	@Override
	public String toString() {
		return "仕入先：" + supplier + "　倉庫：" + warehouse + "　商品：" + name + "　数量, qty=" + qty;
	}
	
}
