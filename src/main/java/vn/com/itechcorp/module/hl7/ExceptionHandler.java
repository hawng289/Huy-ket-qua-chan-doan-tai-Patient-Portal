package vn.com.itechcorp.module.hl7;

import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExceptionHandler implements ReceivingApplicationExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public String processException(String theIncomingMessage, Map<String, Object> theIncomingMetadata, String outgoingMessage, Exception e) {
        logger.error(e.getMessage(), e);
        return outgoingMessage;
    }

}
