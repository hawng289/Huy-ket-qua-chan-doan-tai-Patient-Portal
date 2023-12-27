package vn.com.itechcorp.module.local.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;
import vn.com.itechcorp.module.local.entity.WorkListMessage;

import java.util.List;

@Repository("workListMessageRepository")
public interface WorkListMessageRepository extends AuditableRepository<WorkListMessage, Long> {
    @Query("select w from WorkListMessage w where w.hl7MessageId = ?1")
    List<WorkListMessage> findAllByHl7MessageId(Long hl7MessageId);
}