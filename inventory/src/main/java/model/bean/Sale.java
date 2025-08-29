package model.bean;

import java.time.LocalDate;

public class Sale {
		private int id;
		private LocalDate soldAt;
		private String customerNote;
		public Sale(int id, LocalDate soldAt, String customerNote) {
			super();
			this.id = id;
			this.soldAt = soldAt;
			this.customerNote = customerNote;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public LocalDate getSoldAt() {
			return soldAt;
		}
		public void setSoldAt(LocalDate soldAt) {
			this.soldAt = soldAt;
		}
		public String getCustomerNote() {
			return customerNote;
		}
		public void setCustomerNote(String customerNote) {
			this.customerNote = customerNote;
		}

}
