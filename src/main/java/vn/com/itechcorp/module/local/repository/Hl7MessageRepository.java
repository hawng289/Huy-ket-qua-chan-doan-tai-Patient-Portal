package vn.com.itechcorp.module.local.repository;

import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;
import vn.com.itechcorp.module.local.entity.Hl7Message;

@Repository("hl7MessageRepository")
public interface Hl7MessageRepository extends AuditableRepository<Hl7Message, Long> {

    Hl7Message findByMessageID(String messageID);
}