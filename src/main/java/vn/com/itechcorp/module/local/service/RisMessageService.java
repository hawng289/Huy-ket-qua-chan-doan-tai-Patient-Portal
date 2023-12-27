package vn.com.itechcorp.module.local.service;

import vn.com.itechcorp.base.exception.APIException;
import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.local.entity.RisMessage;
import vn.com.itechcorp.module.local.service.dto.RISMessageDTOGet;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.ris.dto.RisResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RisMessageService extends AuditableDtoService<RISMessageDTOGet, RisMessage, Long> {
    List<RISMessageDTOGet> getByHl7MessageId(Long id) throws APIException;

    CompletableFuture<Long> createOrUpdateAsync(String messageId, RisResponse risResponse, OrderDTO order);

    RISMessageDTOGet getRisMessage(String orderNumber);
}
