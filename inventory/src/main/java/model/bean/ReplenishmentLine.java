package model.bean;





public class ReplenishmentLine {
	private int id;
	private int replId;
	private int productId;
	private int suggestedQty;
	private int approvedQty;
	public ReplenishmentLine(int id, int replId, int productId, int suggestedQty, int approvedQty) {
		super();
		this.id = id;
		this.replId = replId;
		this.productId = productId;
		this.suggestedQty = suggestedQty;
		this.approvedQty = approvedQty;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getReplId() {
		return replId;
	}
	public void setReplId(int replId) {
		this.replId = replId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getSuggestedQty() {
		return suggestedQty;
	}
	public void setSuggestedQty(int suggestedQty) {
		this.suggestedQty = suggestedQty;
	}
	public int getApprovedQty() {
		return approvedQty;
	}
	public void setApprovedQty(int approvedQty) {
		this.approvedQty = approvedQty;
	}

}
