package model.bean;

import java.sql.Timestamp;

public class Warehouse {
	private int id;
	private String name;
	private String location;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	public Warehouse(int id, String name, String location, Timestamp createdAt, Timestamp updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Warehouse(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Warehouse() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
