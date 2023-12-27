package vn.com.itechcorp.module.report.persitance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository("fileSignedRepository")
public interface FileSignedRepository extends AuditableRepository<FileSigned, Long> {
    List<FileSigned> findAllByAccessionNumber(String accessionNumber);

    int countAllByAccessionNumber(String accessionNumber);

    Optional<FileSigned> findByAccessionNumberAndReCallFalse(String accessionNumber);

    @Query("select f from FileSigned f where f.dateCreated < ?1")
    List<FileSigned> findAllByDateCreatedIsLessThan(Date dateCreated);
}