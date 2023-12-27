package vn.com.itechcorp.module.local.service;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.message.OMI_O23;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.hl7.handler.OrderHandler;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.module.local.repository.Hl7MessageRepository;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageType;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.service.RisService;
import vn.com.itechcorp.worklist.dto.WorklistDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.service.WorklistService;

import java.util.Optional;

@Service("resendService")
@RequiredArgsConstructor
public class ResendService {
    private final WorkListMessageService workListMessageService;

    private final Hl7MessageRepository hl7MessageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DefaultHapiContext context;

    private final WorklistService worklistService;

    private final OrderHandler orderHandler;

    private final RisMessageService risMessageService;

    private final RisService risService;

    @Value("${backup.ris.enabled:false}")
    private boolean backupRis;


    public boolean resendToRis(Long id) throws Exception {
        Optional<Hl7Message> hl7Message = this.hl7MessageRepository.findById(id);
        if (hl7Message.isPresent()) {
            Message message = this.context.getGenericParser().parse(hl7Message.get().getHl7());
            RisResponse risResponse = null;
            try {
                // dua vao loai message ma gui di
                OrderDTO orderDTO = this.orderHandler.parse((OMI_O23) message);
                switch (Hl7MessageType.valueOf(hl7Message.get().getMessageType())) {
                    case ORDER_SAVE:
                        logger.info("[Resend-Ris][RowID-{}] CREATE-ORDER again", id);
                        risResponse = this.risService.sendOrder(orderDTO);
                        break;
                    case ORDER_DELETE:
                        logger.info("[Resend-Ris][RowID-{}] DELETE-ORDER again", id);
                        risResponse = this.risService.removeOrder(orderDTO);
                        break;
                }
                this.risMessageService.createOrUpdateAsync(hl7Message.get().getMessageID(), risResponse,orderDTO);
                return risResponse != null;
            } catch (Exception ex) {
                logger.error("[Resend-RIS][RowID-{}] FAILED Resend order", id, ex);
                return false;
            }

        } else {
            logger.info("[Resend-Ris][RowID-{}] Cannot found message", id);
            return false;
        }
    }

    public boolean resendToWl(Long id) throws Exception {
        Optional<Hl7Message> hl7Message = this.hl7MessageRepository.findById(id);
        if (hl7Message.isPresent()) {
            Message message = this.context.getGenericParser().parse(hl7Message.get().getHl7());
            WorklistResponse worklistResponse = null;
            OrderDTO orderDTO = this.orderHandler.parse((OMI_O23) message);
            try {
                // dua vao loai message ma gui di
                switch (Hl7MessageType.valueOf(hl7Message.get().getMessageType())) {
                    case ORDER_SAVE:
                        logger.info("[Resend-WL][RowID-{}] CREATE-ORDER again", id);
                        worklistResponse = this.worklistService.sendWorklist(new WorklistDTO(orderDTO));
                        break;
                    case ORDER_DELETE:
                        logger.info("[Resend-WL][RowID-{}] DELETE-ORDER again", id);
                        worklistResponse = this.worklistService.removeWorklist(orderDTO.getAccessionNumber());
                        break;
                }
                this.workListMessageService.createOrUpdateAsync(hl7Message.get().getMessageID(), worklistResponse,orderDTO);
                return worklistResponse != null;
            } catch (Exception ex) {
                logger.error("[Resend-WL][RowID-{}] FAILED Resend order", id, ex);
                return false;
            }

        } else {
            logger.info("[Resend-WL][RowID-{}] Cannot found message", id);
            return false;
        }
    }
}
