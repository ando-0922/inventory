package model.bean;

import java.sql.Timestamp;

public class Supplier {
	private int id;
	private String name;
	private int leadTimeDays;
	private String phone;
	private String email;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	Supplier(int id, String name, int leadTimeDays, String phone, String email) {
		this.id = id;
		this.name = name;
		this.leadTimeDays = leadTimeDays;
		this.phone = phone;
		this.email = email;
	}
	public Supplier(int id, String name, int leadTimeDays, String phone, String email, Timestamp createdAt,
			Timestamp updatedAt) {
		this.id = id;
		this.name = name;
		this.leadTimeDays = leadTimeDays;
		this.phone = phone;
		this.email = email;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public Supplier(String name, String leadTimeDays, String phone, String mail) {
		this.name = name;
		setLeadTimeDays(leadTimeDays);
		this.phone = phone;
		this.email = mail;
	}
	public Supplier(int id2, String name2) {
		this.id = id2;
		this.name = name2;
	}
	public Supplier() {
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
	public int getLeadTimeDays() {
		return leadTimeDays;
	}
	public void setLeadTimeDays(String ltd) {
		int leadTimeDays = Integer.parseInt(ltd);
		this.leadTimeDays = leadTimeDays;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	@Override
	public String toString() {
		return "Supplier [name=" + name + ", leadTimeDays=" + leadTimeDays + ", phone=" + phone + ", email=" + email
				+ "]";
	}
	public void setLeadTimeDays(int int1) {
		
	}
	
}
