package vn.com.itechcorp.module.lockorder.service;

import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.service.impl.AuditableDtoJpaServiceImpl;
import vn.com.itechcorp.module.lockorder.persistance.LockOrder;
import vn.com.itechcorp.module.lockorder.persistance.LockOrderRepository;
import vn.com.itechcorp.module.lockorder.service.dto.LockOrderDTOGet;

@Service("lockOrderService")
public class LockOrderServiceImpl extends AuditableDtoJpaServiceImpl<LockOrderDTOGet, LockOrder,Long> implements LockOrderService {

    private final LockOrderRepository lockOrderRepository;

    public LockOrderServiceImpl(LockOrderRepository lockOrderRepository) {
        this.lockOrderRepository = lockOrderRepository;
    }

    @Override
    public LockOrderRepository getRepository() {
        return lockOrderRepository;
    }

    @Override
    public LockOrderDTOGet convert(LockOrder lockOrder) {
        return new LockOrderDTOGet(lockOrder);
    }
}
