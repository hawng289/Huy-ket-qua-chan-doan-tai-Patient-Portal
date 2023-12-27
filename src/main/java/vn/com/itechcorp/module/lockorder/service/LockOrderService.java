package vn.com.itechcorp.module.lockorder.service;

import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.lockorder.persistance.LockOrder;
import vn.com.itechcorp.module.lockorder.service.dto.LockOrderDTOGet;

public interface LockOrderService extends AuditableDtoService<LockOrderDTOGet, LockOrder,Long> {
}
