package model.bean;

import java.time.LocalDate;

public class AuditLog {
	private int id;
	private String actor = "demo-user";
	private String action;
	private String entity;
	private int entityId;
	private String detail;
	private LocalDate loggedAt;
	public AuditLog(int id, String actor, String action, String entity, int entityId, String detail) {
		super();
		this.id = id;
		this.actor = actor;
		this.action = action;
		this.entity = entity;
		this.entityId = entityId;
		this.detail = detail;
	}
	AuditLog(String action, String entity, int id, LocalDate at) {
		this.action = action;
		this.entity = entity;
		this.entityId = id;
		this.loggedAt = at;
	}
	public AuditLog(String action, String entity, int entityId) {
		this.action = action;
		this.entity = entity;
		this.entityId = entityId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public LocalDate getLoggedAt() {
		return loggedAt;
	}
	public void setLoggedAt(LocalDate loggedAt) {
		this.loggedAt = loggedAt;
	}
	
}
