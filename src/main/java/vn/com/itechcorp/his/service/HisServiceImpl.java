package vn.com.itechcorp.his.service;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.message.ACK;
import ca.uhn.hl7v2.protocol.Transportable;
import ca.uhn.hl7v2.protocol.impl.TransportableImpl;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.his.dto.HisResponse;
import vn.com.itechcorp.his.proxy.HisProxy;
import vn.com.itechcorp.module.hl7.MyApplicationRouter;
import vn.com.itechcorp.module.hl7.util.Constants;
import vn.com.itechcorp.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("hisService")
public class HisServiceImpl implements HisService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MyApplicationRouter myApplicationRouter;

    @Autowired
    private HapiContext hapiContext;

    @Autowired
    private HisProxy hisProxy;

    @Override
    public HisResponse processMessage(HisRequest object) {
        String messageId = UUID.randomUUID().toString();
        try {
            Map<String, Object> metaData = new HashMap<>();
            logger.info("[MsgID-{}] Received HL7 RestfulAPI Request from HIS, Raw message-{}", messageId,object.getHl7Message().replaceAll("\r","\n"));
            metaData.put(Constants.MESSAGE_ID, messageId);
            Transportable processed = this.myApplicationRouter.processMessage(new TransportableImpl(object.getHl7Message(), metaData));
            Message message = hapiContext.getGenericParser().parse(processed.getMessage());
            ACK ackMessage = (ACK) message;
            String ackCode = ackMessage.getMSA().getMsa1_AcknowledgmentCode().getValue();
            if (ackCode.equals(AcknowledgmentCode.AA.name())) {
                logger.info("[MsgID-{}] SUCCEED - Ack code response is AA", messageId);
                return new HisResponse(true);
            }
            logger.info("[MsgID-{}] FAILED - Ack code response {}", messageId, ackCode);
            return new HisResponse(false, processed.getMessage());
        } catch (Exception ex) {
            logger.error("[MsgID-{}] FAILED - Exception {}:", messageId, ex.getMessage());
            return new HisResponse(false, ex.getMessage());
        }
    }

    @Override
    public HisResponse sendReport(HisRequest object) {
        try (Response response = hisProxy.sendReport(object)) {
            return JsonUtils.getInstance().convertFeignResponse(response, HisResponse.class);
        } catch (Exception ex) {
            logger.error("[REPORT] ERROR send report to HIS", ex);
            return new HisResponse(false, ex.getMessage());
        }
    }

    @Override
    public HisResponse deleteReport(HisRequest object) {
        try (Response response = hisProxy.deleteReport(object)) {
            return JsonUtils.getInstance().convertFeignResponse(response, HisResponse.class);
        } catch (Exception ex) {
            logger.error("[REPORT] ERROR delete report to HIS", ex);
            return new HisResponse(false, ex.getMessage());
        }
    }
}
