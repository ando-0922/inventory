package model.bean;

import java.sql.Timestamp;

public class StorageHistory {
	private int productId;
	private int warehouseId;
	private String name;
	private int qty;
	private String type;
	private String refType;
	private int refId;
	private Timestamp movedAt;
	
	public StorageHistory(String name, int qty, String type, String refType, Timestamp movedAt) {
		this.name = name;
		this.qty = qty;
		this.type = type;
		this.refType = refType;
		this.movedAt = movedAt;
	}
	public StorageHistory(int productId, int warehouseId, int qty, String type, String refType, int refId) {
		this.productId = productId;
		this.warehouseId = warehouseId;
		this.qty = qty;
		this.type = type;
		this.refType = refType;
		this.refId = refId;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public Timestamp getMovedAt() {
		return movedAt;
	}
	public void setMovedAt(Timestamp movedAt) {
		this.movedAt = movedAt;
	}
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public int getRefId() {
		return refId;
	}
	public void setRefId(int refId) {
		this.refId = refId;
	}
	@Override
	public String toString() {
		return "商品名：" + name + "\t在庫数：" + qty + "\t区分" + type + "\t参照区別" + refType+ "\t入出庫日時：" + movedAt;
	}
	
}
