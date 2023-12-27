package vn.com.itechcorp.module.local.repository;

import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.BaseRepository;
import vn.com.itechcorp.module.local.entity.PatientPortalReport;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository("patientPortalReportRepository")
public interface PatientPortalReportRepository extends BaseRepository<PatientPortalReport, Long> {
    List<PatientPortalReport> findByAccessionNumberAndProcedureCode(@NotNull String accessionNumber, @NotNull String procedureCode);
}
