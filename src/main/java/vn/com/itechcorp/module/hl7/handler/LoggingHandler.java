package vn.com.itechcorp.module.hl7.handler;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.local.service.Hl7MessageService;

import java.util.Map;

@Service("loggingHandler")
@RequiredArgsConstructor
public class LoggingHandler extends BaseHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Hl7MessageService hl7Service;

    @Override
    public Hl7MessageService getHl7MessageService() {
        return hl7Service;
    }

    @Override
    public Message process(String messageId, Message message, Map<String, Object> map) throws Exception {
        logger.info("[MsgId-{}] unsupported message type", messageId);
        return message.generateACK(AcknowledgmentCode.AE, new HL7Exception("Unsupported message type"));
    }

    @Override
    public boolean canProcess(Message message) {
        return true;
    }
}
