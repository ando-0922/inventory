package model.bean;

import java.sql.Timestamp;

public class Product {
	private int id;
	private String jan;
	private String name;
	private double stdCost;
	private double stdPrice;
	private int reorderPoint;
	private int orderLot;
	private boolean discontinued;
	private Timestamp createdAt;
	private Timestamp updateAt;
	public Product(int id, String jan, String name, double stdCost, double stdPrice, int reorderPoint, int orderLot,
			boolean discontinued, Timestamp createdAt, Timestamp updateAt) {
		this.id = id;
		this.jan = jan;
		this.name = name;
		this.stdCost = stdCost;
		this.stdPrice = stdPrice;
		this.reorderPoint = reorderPoint;
		this.orderLot = orderLot;
		this.discontinued = discontinued;
		this.createdAt = createdAt;
		this.updateAt = updateAt;
	}
	public Product(String jan, String name, double stdCost, double stdPrice, int reorderPoint, int orderLot) {
		this.jan = jan;
		this.name = name;
		this.stdCost = stdCost;
		this.stdPrice = stdPrice;
		this.reorderPoint = reorderPoint;
		this.orderLot = orderLot;
	}
	public Product(String name, int stdCost, int stdPrice, int reorderPoint, int orderLot) {
		this.name = name;
		this.stdCost = stdCost;
		this.stdPrice = stdPrice;
		this.reorderPoint = reorderPoint;
		this.orderLot = orderLot;
	}
	public Product(String name2, String jan2) {
		this.name = name2;
		this.jan = jan2;
	}
	public Product() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public double getStdCost() {
		return stdCost;
	}
	public void setStdCost(double stdCost) {
		this.stdCost = stdCost;
	}
	public double getStdPrice() {
		return stdPrice;
	}
	public void setStdPrice(double stdPrice) {
		this.stdPrice = stdPrice;
	}
	public int getReorderPoint() {
		return reorderPoint;
	}
	public void setReorderPoint(int reorderPoint) {
		this.reorderPoint = reorderPoint;
	}
	public int getOrderLot() {
		return orderLot;
	}
	public void setOrderLot(int orderLot) {
		this.orderLot = orderLot;
	}
	public boolean isDiscontinued() {
		return discontinued;
	}
	public void setDiscontinued(boolean discontinued) {
		this.discontinued = discontinued;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Timestamp updateAt) {
		this.updateAt = updateAt;
	}
	
}
