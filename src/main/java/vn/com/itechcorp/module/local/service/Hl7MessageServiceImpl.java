package vn.com.itechcorp.module.local.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.service.impl.AuditableDtoJpaServiceImpl;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.module.local.repository.Hl7MessageRepository;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOGet;

@Service("hl7MessageService")
@RequiredArgsConstructor
public class Hl7MessageServiceImpl extends AuditableDtoJpaServiceImpl<Hl7MessageDTOGet, Hl7Message, Long> implements Hl7MessageService {

    private final Hl7MessageRepository hl7MessageRepository;

    @Override
    public Hl7MessageRepository getRepository() {
        return this.hl7MessageRepository;
    }

    @Override
    public Hl7MessageDTOGet convert(Hl7Message hl7Message) {
        if (hl7Message == null) return null;
        return new Hl7MessageDTOGet(hl7Message);
    }

}
