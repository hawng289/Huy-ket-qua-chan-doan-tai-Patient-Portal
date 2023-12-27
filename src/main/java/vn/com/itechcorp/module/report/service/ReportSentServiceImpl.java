package vn.com.itechcorp.module.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.service.impl.AuditableDtoJpaServiceImpl;
import vn.com.itechcorp.module.report.persitance.ReportSent;
import vn.com.itechcorp.module.report.persitance.ReportSentRepository;
import vn.com.itechcorp.module.report.service.dto.reportsent.ReportSendDTOGet;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("reportSentService")
public class ReportSentServiceImpl extends AuditableDtoJpaServiceImpl<ReportSendDTOGet, ReportSent, Long> implements ReportSentService {

    @Autowired
    private ReportSentRepository reportSentRepository;

    @Override
    public ReportSentRepository getRepository() {
        return reportSentRepository;
    }

    @Override
    public ReportSendDTOGet convert(ReportSent reportSent) {
        if (reportSent == null) return null;
        return new ReportSendDTOGet(reportSent);
    }

    @Override
    public boolean reportIsSentToHis(String accessionNumber) {
        //1. His status true and Have file pdf
        boolean condition1 = !getRepository().findByAccessionNumberAndHisStatusTrueAndFileIdNotNull(accessionNumber).isEmpty();

        //2. If type CA newest in createDate
        List<ReportSent> allReportSent = getRepository().findAllByAccessionNumber(accessionNumber).stream().filter(ReportSent::isHisStatus).sorted(Comparator.comparing(ReportSent::getDateCreated).reversed()).collect(Collectors.toList());

        if (allReportSent.isEmpty()) return condition1;

        if (allReportSent.get(0).getFileId() == null){
            return false;
        }

        return condition1;
    }

    @Override
    public boolean isSentReportToHisSucceed(Long reportId) {
        return getRepository().findByReportIdAndHisStatusTrue(reportId).isPresent();
    }
}
