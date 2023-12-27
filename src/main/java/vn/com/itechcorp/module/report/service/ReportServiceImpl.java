package vn.com.itechcorp.module.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.exception.APIException;
import vn.com.itechcorp.base.service.dto.BaseDtoCreate;
import vn.com.itechcorp.base.service.impl.BaseDtoJpaServiceImpl;
import vn.com.itechcorp.module.report.persitance.Report;
import vn.com.itechcorp.module.report.persitance.ReportRepository;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOCreate;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOGet;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOUpdate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("reportService")
public class ReportServiceImpl extends BaseDtoJpaServiceImpl<ReportDTOGet, Report, Long> implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public ReportRepository getRepository() {
        return this.reportRepository;
    }

    @Override
    protected Report validateAndCreateEntry(BaseDtoCreate<Report, Long> entity) throws APIException {
        ReportDTOCreate object = (ReportDTOCreate) entity;
        // 1. If have report before, update they to reCall = true
        List<Report> existReports = this.findEntryByAccessionNumber(object.getAccessionNumber());
        if (!existReports.isEmpty()) {
            // Update status
            existReports.forEach(report -> {
                ReportDTOUpdate dtoUpdate = new ReportDTOUpdate();
                dtoUpdate.setId(report.getId());
                dtoUpdate.setReCall(true);
                update(dtoUpdate);
            });
        }
        return object.toEntry();
    }
    @Override
    public ReportDTOGet convert(Report report) {
        if (report == null) return null;
        return new ReportDTOGet(report);
    }

    @Override
    public Set<String> getSortableColumns() {
        Set<String> columns = new HashSet<>();
        columns.add("messageTime");
        return columns;
    }

    private List<Report> findEntryByAccessionNumber(String accessionNumber) {
        return getRepository().findAllByAccessionNumber(accessionNumber);
    }
}
