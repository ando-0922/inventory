package controller;

import model.bean.AuditLog;
import model.dao.AuditLogsDAO;

public class AuditLogsService {
	AuditLogsDAO dao = new AuditLogsDAO();

	public void insertLog(AuditLog a) {
		dao.insertLog(a);
	}

}
