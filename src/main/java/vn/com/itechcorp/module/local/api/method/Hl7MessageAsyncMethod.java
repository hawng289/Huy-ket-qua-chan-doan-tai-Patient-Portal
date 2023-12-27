package vn.com.itechcorp.module.local.api.method;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.message.OMI_O23;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.base.api.method.AsyncAuditableDtoAPIMethod;
import vn.com.itechcorp.base.api.response.APIResponse;
import vn.com.itechcorp.base.api.response.APIResponseHeader;
import vn.com.itechcorp.base.api.response.APIResponseStatus;
import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.hl7.handler.OrderHandler;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.RisMessageService;
import vn.com.itechcorp.module.local.service.WorkListMessageService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageType;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.service.RisService;
import vn.com.itechcorp.worklist.dto.WorklistDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.service.WorklistService;

@Component
public class Hl7MessageAsyncMethod extends AsyncAuditableDtoAPIMethod<Hl7MessageDTOGet, Hl7Message, Long> {
    private final Hl7MessageService hl7MessageService;

    private final RisMessageService risMessageService;

    private final WorkListMessageService workListMessageService;

    private final RisService risService;

    private final WorklistService worklistService;

    private final OrderHandler orderHandler;

    private final DefaultHapiContext context;

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    public Hl7MessageAsyncMethod(AuditableDtoService<Hl7MessageDTOGet, Hl7Message, Long> service,
                                 Hl7MessageService hl7MessageService,
                                 RisMessageService risMessageService,
                                 WorkListMessageService workListMessageService,
                                 RisService risService,
                                 WorklistService worklistService,
                                 OrderHandler orderHandler, DefaultHapiContext context) {
        super(service);
        this.hl7MessageService = hl7MessageService;
        this.risMessageService = risMessageService;
        this.workListMessageService = workListMessageService;
        this.risService = risService;
        this.worklistService = worklistService;
        this.orderHandler = orderHandler;
        this.context = context;
    }

    @Override
    public AuditableDtoService<Hl7MessageDTOGet, Hl7Message, Long> getService() {
        return this.hl7MessageService;
    }

    public ResponseEntity<APIResponse<Object>> reSendMessage(Long messageId, boolean sendToRis, boolean sendToWL) {
        try {
            // tim kiem message
            Hl7MessageDTOGet hl7Message = this.hl7MessageService.getById(messageId);
            if (hl7Message == null)
                return new ResponseEntity<>(new APIResponse<>(new APIResponseHeader(APIResponseStatus.NOT_FOUND, "Not found message id = " + messageId), null), HttpStatus.BAD_REQUEST);
            // parse message
            Message message = this.context.getGenericParser().parse(hl7Message.getHl7());
            if (sendToRis) {
                RisResponse risResponse = null;
                // dua vao loai message ma gui di
                OrderDTO order = this.orderHandler.parse((OMI_O23) message);
                switch (Hl7MessageType.valueOf(hl7Message.getMessageType())) {
                    case ORDER_SAVE:
                        risResponse = this.risService.sendOrder(order);
                        break;
                    case ORDER_DELETE:
                        risResponse = this.risService.removeOrder(order);
                        break;
                }
                this.risMessageService.createOrUpdateAsync(hl7Message.getMessageID(), risResponse,order);
                return new ResponseEntity<>(new APIResponse<>(new APIResponseHeader(APIResponseStatus.OK, "Message resend succeed"), risResponse), HttpStatus.OK);
            }
            if (sendToWL) {
                WorklistResponse worklistResponse = null;
                OrderDTO orderDTO = this.orderHandler.parse((OMI_O23) message);
                switch (Hl7MessageType.valueOf(hl7Message.getMessageType())) {
                    case ORDER_SAVE:
                        worklistResponse = this.worklistService.sendWorklist(new WorklistDTO(orderDTO));
                        break;
                    case ORDER_DELETE:
                        this.worklistService.removeWorklist(orderDTO.getAccessionNumber());
                        break;
                }
                this.workListMessageService.createOrUpdateAsync(hl7Message.getMessageID(), worklistResponse,orderDTO);
                return new ResponseEntity<>(new APIResponse<>(new APIResponseHeader(APIResponseStatus.OK, "Message resend succeed"), worklistResponse), HttpStatus.OK);
            }


        } catch (Exception ex) {
            this.getLogger().error(ex.getMessage());
            return new ResponseEntity<>(new APIResponse<>(new APIResponseHeader(APIResponseStatus.NOT_FOUND, ex.getMessage()), null), HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
