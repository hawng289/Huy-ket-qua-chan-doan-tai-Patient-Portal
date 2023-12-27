package vn.com.itechcorp.worklist.job;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.base.service.filter.PaginationInfo;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.ResendService;
import vn.com.itechcorp.module.local.service.WorkListMessageService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.WLMessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.WLMessageFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RetrySendWLMessage {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Hl7MessageService hl7MessageService;

    private final WorkListMessageService workListMessageService;

    private final ResendService resendService;

    @Value("${max.retry.send}")
    private String maxRetry;

    @Scheduled(fixedDelayString = "${time.delay.send.retry:5000}", initialDelay = 30000)
    public void sendToWorkList() throws Exception {
        List<WLMessageDTOGet> elements = this.workListMessageService.getPageOfData(new WLMessageFilter(false, Integer.parseInt(maxRetry)),
                new PaginationInfo(0, 20)).getElements();

        logger.info("[RE-SEND][WL] Found {} message need to sent", elements.size());

        if (elements.isEmpty()) return;

        for (WLMessageDTOGet el : elements) {
            // get hl7 from hl7_message
            Hl7MessageDTOGet messageIsNotSent = this.hl7MessageService.getById(el.getHl7MessageId());

            logger.info("[WL-RowID-{}] resending to worklist", messageIsNotSent.getId());

            this.resendService.resendToWl(messageIsNotSent.getId());
        }
    }
}
