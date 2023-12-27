package vn.com.itechcorp.module.report.service;

import vn.com.itechcorp.base.service.BaseDtoService;
import vn.com.itechcorp.module.report.persitance.Report;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOGet;
import vn.com.itechcorp.ris.dto.PdfReportDTO;

public interface ReportService extends BaseDtoService<ReportDTOGet, Report, Long> {
}
