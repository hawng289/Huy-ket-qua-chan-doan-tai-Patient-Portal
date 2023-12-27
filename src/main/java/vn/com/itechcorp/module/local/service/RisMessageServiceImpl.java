package vn.com.itechcorp.module.local.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.exception.APIException;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;
import vn.com.itechcorp.base.service.impl.AuditableDtoJpaServiceImpl;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.module.local.entity.RisMessage;
import vn.com.itechcorp.module.local.repository.Hl7MessageRepository;
import vn.com.itechcorp.module.local.repository.RisMessageRepository;
import vn.com.itechcorp.module.local.service.dto.RISMessageDTOCreate;
import vn.com.itechcorp.module.local.service.dto.RISMessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.RISMessageDTOUpdate;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.ris.dto.RisResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service("risMessageService")
@RequiredArgsConstructor
public class RisMessageServiceImpl extends AuditableDtoJpaServiceImpl<RISMessageDTOGet, RisMessage, Long> implements RisMessageService {

    private final RisMessageRepository risMessageRepository;

    private final Hl7MessageRepository hl7MessageRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ris.url}")
    private String RIS_URL;

    @Override
    public AuditableRepository<RisMessage, Long> getRepository() {
        return this.risMessageRepository;
    }

    @Override
    public RISMessageDTOGet convert(RisMessage risMessage) {
        if (risMessage == null) return null;
        return new RISMessageDTOGet(risMessage);
    }

    @Override
    public List<RISMessageDTOGet> getByHl7MessageId(Long id) throws APIException {
        return convertAndFetchLazyInformation(this.risMessageRepository.findAllByHl7MessageId(id));
    }

    @Override
    @Async
    public CompletableFuture<Long> createOrUpdateAsync(String messageId, RisResponse risResponse, OrderDTO order) {
        return CompletableFuture.completedFuture(this.createOrUpdate(messageId, risResponse,order));
    }

    public Long createOrUpdate(String messageId, RisResponse risResponse,OrderDTO order) {
        Hl7Message hl7Message = this.hl7MessageRepository.findByMessageID(messageId);
        List<RisMessage> existMessage = this.risMessageRepository.findAllByHl7MessageId(hl7Message.getId());
        if (existMessage.isEmpty()) {
            // create
            // check ris response: if code in response header = 453 (duplicate orderNumber) or 2xx --> true
            logger.info("[MsgID-{}][RIS-RESPONSE] save new response with rowID {}", messageId, hl7Message.getId());
            RISMessageDTOCreate dtoCreate = new RISMessageDTOCreate();
            dtoCreate.setHl7MessageId(hl7Message.getId());
            dtoCreate.setRequestUrl(RIS_URL);
            dtoCreate.setRequestCount(1);
            dtoCreate.setErrors(risResponse.toString());
            dtoCreate.setAccessionNumber(order.getAccessionNumber());
            dtoCreate.setOrderNumber(order.getOrderNumber());
            dtoCreate.setPid(order.getPatient().getPid());
            dtoCreate.setSucceeded(risResponse.getHeader() != null
                    && (risResponse.getHeader().getCode() == 453
                    || HttpStatus.valueOf(risResponse.getHeader().getCode()).is2xxSuccessful()));
            return create(dtoCreate,0L);
        } else {
            // update
            // check ris response:
            logger.info("[MsgID-{}][RIS-RESPONSE] update existed response with rowID {}", messageId, hl7Message.getId());
            RISMessageDTOUpdate updateEntry = new RISMessageDTOUpdate();
            updateEntry.setId(existMessage.get(0).getId());
            updateEntry.setRequestCount(existMessage.get(0).getRequestCount() + 1);
            boolean isTrue = false;
            if (risResponse.getHeader() != null) {
                isTrue = risResponse.getHeader().getCode() == 453 || HttpStatus.valueOf(risResponse.getHeader().getCode()).is2xxSuccessful();
            }
            updateEntry.setSucceeded(isTrue);
            updateEntry.setErrors(risResponse.toString());
            return this.update(updateEntry, 0L);
        }
    }

    @Override
    public RISMessageDTOGet getRisMessage(String orderNumber){
        List<RisMessage> allRisMessage = risMessageRepository.findAllByOrderNumber(orderNumber);
        RisMessage risMessage = allRisMessage.stream().filter(e -> e.getAccessionNumber() != null && e.getPid() != null).findFirst().orElse(null);
        return convert(risMessage);
    }
}
