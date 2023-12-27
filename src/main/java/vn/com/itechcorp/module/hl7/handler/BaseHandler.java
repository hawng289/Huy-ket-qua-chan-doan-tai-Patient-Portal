package vn.com.itechcorp.module.hl7.handler;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.message.ADT_A06;
import ca.uhn.hl7v2.model.v27.message.OMI_O23;
import ca.uhn.hl7v2.model.v27.segment.ORC;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.com.itechcorp.module.hl7.util.Constants;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOCreate;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageType;

import java.util.Map;
import java.util.UUID;

@Getter
public abstract class BaseHandler implements ReceivingApplication<Message> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Hl7MessageType getMessageType(Message message) {
        // check ban tin OMI_O23
        if (message instanceof OMI_O23) {
            ORC orcSegment = ((OMI_O23) message).getORDER().getORC();
            if (orcSegment.getOrc1_OrderControl().getValue().equals("NW") || orcSegment.getOrc1_OrderControl().getValue().equals("XO"))
                return Hl7MessageType.ORDER_SAVE;
            else return Hl7MessageType.ORDER_DELETE;
        }
        // check ban tin ADT_A06
        if (message instanceof ADT_A06) {
            // hien tai chi co ban tin cap nhat thong tin benh nhan
            return Hl7MessageType.PATIENT_UPDATE;
        }
        return Hl7MessageType.OTHER;
    }

    public abstract Hl7MessageService getHl7MessageService();

    public abstract Message process(String messageId, Message message, Map<String, Object> map) throws Exception;

    @Override
    public Message processMessage(Message message, Map<String, Object> map) {
        // tao moi messageID neu chua ton tai
        String messageId = map.containsKey(Constants.MESSAGE_ID) ?
                (String) map.get(Constants.MESSAGE_ID) : UUID.randomUUID().toString();

        // luu log cac ban tin HL7
        Long id = getHl7MessageService().create(new Hl7MessageDTOCreate(message.getMessage().toString(), getMessageType(message).toString(), messageId), 0L);
        logger.info("[MsgID-{}] Created hl7 rowID-{}", messageId, id);
        try {
            return process(messageId, message, map);
        } catch (Exception ex) {
            logger.error("[MsgID-{}] Errors: ", messageId, ex);
            try {
                return message.generateACK(AcknowledgmentCode.AE, new HL7Exception(ex.getMessage()));
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
