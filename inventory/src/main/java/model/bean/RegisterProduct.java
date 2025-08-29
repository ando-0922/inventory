package model.bean;

import java.sql.Timestamp;

public class RegisterProduct {
	private int id;
	private String jan;
	private String name;
	private double stdCost;
	private double stdPrice;
	private int reoderPoint;
	private int orderLot;
	private boolean discontinued;
	private Timestamp createdAt;
	private Timestamp updateAt;
	public RegisterProduct(int id, String jan, String name, double stdCost, double stdPrice, int reoderPoint,
			int orderLot, boolean discontinued, Timestamp createdAt, Timestamp updateAt) {
		super();
		this.id = id;
		this.jan = jan;
		this.name = name;
		this.stdCost = stdCost;
		this.stdPrice = stdPrice;
		this.reoderPoint = reoderPoint;
		this.orderLot = orderLot;
		this.discontinued = discontinued;
		this.createdAt = createdAt;
		this.updateAt = updateAt;
	}
	public RegisterProduct(String jan, String name, int stdCost, int stdPrice, int reoderPoint, int orderLot) {
		this.jan = jan;
		this.name = name;
		this.stdCost = stdCost;
		this.stdPrice = stdPrice;
		this.reoderPoint = reoderPoint;
		this.orderLot = orderLot;
	}
	public RegisterProduct(String name, int stdCost, int stdPrice, int reoderPoint, int orderLot) {
		this.name = name;
		this.stdCost = stdCost;
		this.stdPrice = stdPrice;
		this.reoderPoint = reoderPoint;
		this.orderLot = orderLot;
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
	public int getReoderPoint() {
		return reoderPoint;
	}
	public void setReoderPoint(int reoderPoint) {
		this.reoderPoint = reoderPoint;
	}
	public int getOrderLot() {
		return orderLot;
	}
	public void setOrderlot(int orderLot) {
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
	@Override
	public String toString() {
		return "RegisterProduct [jan=" + jan + ", name=" + name + ", stdCost=" + stdCost + ", stdPrice=" + stdPrice
				+ ", reoderPoint=" + reoderPoint + ", orderlot=" + orderLot + "]";
	}
	
}
