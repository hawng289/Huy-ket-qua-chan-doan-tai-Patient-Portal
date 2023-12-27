package vn.com.itechcorp.module.local.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;
import vn.com.itechcorp.module.local.entity.RisMessage;

import java.util.List;

@Repository("risMessageRepository")
public interface RisMessageRepository extends AuditableRepository<RisMessage, Long> {
    @Query("select r from RisMessage r where r.hl7MessageId = ?1")
    List<RisMessage> findAllByHl7MessageId(Long hl7MessageId);

    List<RisMessage> findAllByOrderNumber(String orderNumber);
}