package vn.com.itechcorp.module.report.persitance;

import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;

import java.util.List;
import java.util.Optional;

@Repository("reportSentRepository")
public interface ReportSentRepository extends AuditableRepository<ReportSent, Long> {
    List<ReportSent> findByAccessionNumberAndHisStatusTrueAndFileIdNotNull(String accessionNumber);

    int countAllByReportId(Long reportId);

    Optional<ReportSent> findByReportIdAndHisStatusTrue(Long reportId);

    List<ReportSent> findAllByAccessionNumber(String accessionNumber);
}