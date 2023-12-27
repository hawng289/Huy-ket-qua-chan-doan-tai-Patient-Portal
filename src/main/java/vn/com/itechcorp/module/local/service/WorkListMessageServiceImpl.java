package vn.com.itechcorp.module.local.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.persistence.repository.AuditableRepository;
import vn.com.itechcorp.base.service.impl.AuditableDtoJpaServiceImpl;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.module.local.entity.WorkListMessage;
import vn.com.itechcorp.module.local.repository.Hl7MessageRepository;
import vn.com.itechcorp.module.local.repository.WorkListMessageRepository;
import vn.com.itechcorp.module.local.service.dto.WLMessageDTOCreate;
import vn.com.itechcorp.module.local.service.dto.WLMessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.WLMessageDTOUpdate;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service("workListMessageService")
@RequiredArgsConstructor
public class WorkListMessageServiceImpl extends AuditableDtoJpaServiceImpl<WLMessageDTOGet, WorkListMessage, Long> implements WorkListMessageService {

    private final WorkListMessageRepository workListMessageRepository;

    private final Hl7MessageRepository hl7MessageRepository;

    @Value("${worklist.server.url}")
    private String WL_URL;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public AuditableRepository<WorkListMessage, Long> getRepository() {
        return this.workListMessageRepository;
    }

    @Override
    public WLMessageDTOGet convert(WorkListMessage workListMessage) {
        if (workListMessage == null) return null;
        return new WLMessageDTOGet(workListMessage);
    }

    @Override
    @Async
    public CompletableFuture<Long> createOrUpdateAsync(String messageId, WorklistResponse workListResponse, OrderDTO order) {
        return CompletableFuture.completedFuture(this.createOrUpdate(messageId, workListResponse,order));
    }

    public Long createOrUpdate(String messageId, WorklistResponse workListResponse,OrderDTO order) {

        Hl7Message hl7Message = this.hl7MessageRepository.findByMessageID(messageId);

        List<WorkListMessage> existMessage = this.workListMessageRepository.findAllByHl7MessageId(hl7Message.getId());
        if (existMessage.isEmpty()) {
            // create new record in wl_message
            logger.info("[MsgID-{}][WL-RESPONSE] save new response with rowID {}", messageId, hl7Message.getId());
            WLMessageDTOCreate dtoCreate = new WLMessageDTOCreate();
            dtoCreate.setHl7MessageId(hl7Message.getId());
            dtoCreate.setRequestUrl(WL_URL);
            dtoCreate.setRequestCount(1);
            dtoCreate.setSucceeded(workListResponse.isSucceed());
            dtoCreate.setErrors(workListResponse.toString());
            dtoCreate.setOrderNumber(order.getOrderNumber());
            dtoCreate.setAccessionNumber(order.getAccessionNumber());
            return create(dtoCreate,0L);
        } else {
            // update in wl_message
            logger.info("[MsgID-{}][WL-RESPONSE] update exist response with rowID {}", messageId, hl7Message.getId());
            WLMessageDTOUpdate update = new WLMessageDTOUpdate();
            update.setId(existMessage.get(0).getId());
            update.setRequestCount(existMessage.get(0).getRequestCount() + 1);
            update.setSucceeded(workListResponse.isSucceed());
            update.setErrors(workListResponse.toString());
            return this.update(update, 0L);
        }
    }
}
