package model.bean;

import java.time.LocalDate;

public class Replenishment {
	
		private int id;
		private String status;
		private LocalDate approvedAt;

		Replenishment(int id, String status, LocalDate approvedAt) {
			this.id = id;
			this.status = status;
			this.approvedAt = approvedAt;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public LocalDate getApprovedAt() {
			return approvedAt;
		}

		public void setApprovedAt(LocalDate approvedAt) {
			this.approvedAt = approvedAt;
		}
	

}
