package vn.com.itechcorp.module.lockorder.persistance;

import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;

@Repository("lockOrderRepository")
public interface LockOrderRepository extends AuditableRepository<LockOrder, Long> {
}