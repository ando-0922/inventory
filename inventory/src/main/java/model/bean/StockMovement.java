package model.bean;

import java.time.LocalDate;

public class StockMovement {
	private int id;
	private int productId;
	private int warehouseId;
	private int qty;
	private String type;
	private String refType;
	private int refId;
	private LocalDate movedAt;
	public StockMovement(int id, int productId, int warehouseId, int qty, String type, String refType, int refId,
			LocalDate movedAt) {
		super();
		this.id = id;
		this.productId = productId;
		this.warehouseId = warehouseId;
		this.qty = qty;
		this.type = type;
		this.refType = refType;
		this.refId = refId;
		this.movedAt = movedAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getRefId() {
		return refId;
	}
	public void setRefId(int refId) {
		this.refId = refId;
	}
	public LocalDate getMovedAt() {
		return movedAt;
	}
	public void setMovedAt(LocalDate movedAt) {
		this.movedAt = movedAt;
	}

	
}
