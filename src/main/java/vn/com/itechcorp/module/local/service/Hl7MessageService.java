package vn.com.itechcorp.module.local.service;

import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOGet;

public interface Hl7MessageService extends AuditableDtoService<Hl7MessageDTOGet, Hl7Message, Long> {
}
