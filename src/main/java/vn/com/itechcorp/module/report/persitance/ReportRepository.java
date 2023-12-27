package vn.com.itechcorp.module.report.persitance;

import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository("reportRepository")
public interface ReportRepository extends BaseRepository<Report, Long> {

    List<Report> findAllByAccessionNumber(String accessionNumber);

    Optional<Report> findByAccessionNumberAndReCallFalse(String accessionNumber);

}