package vn.com.itechcorp.module.local.service;

import org.springframework.scheduling.annotation.Async;
import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.local.entity.WorkListMessage;
import vn.com.itechcorp.module.local.service.dto.WLMessageDTOGet;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;

import java.util.concurrent.CompletableFuture;

public interface WorkListMessageService extends AuditableDtoService<WLMessageDTOGet, WorkListMessage, Long> {


    @Async
    CompletableFuture<Long> createOrUpdateAsync(String messageId, WorklistResponse workListResponse, OrderDTO order);

}
