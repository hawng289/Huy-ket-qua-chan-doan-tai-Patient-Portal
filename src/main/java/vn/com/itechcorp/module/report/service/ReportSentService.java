package vn.com.itechcorp.module.report.service;

import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.report.persitance.ReportSent;
import vn.com.itechcorp.module.report.service.dto.reportsent.ReportSendDTOGet;

public interface ReportSentService extends AuditableDtoService<ReportSendDTOGet, ReportSent, Long> {
    boolean reportIsSentToHis(String accessionNumber);

    boolean isSentReportToHisSucceed(Long reportId);
}
