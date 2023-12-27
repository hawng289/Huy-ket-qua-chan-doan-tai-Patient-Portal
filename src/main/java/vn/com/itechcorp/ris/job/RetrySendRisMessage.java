package vn.com.itechcorp.ris.job;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.base.service.filter.PaginationInfo;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.ResendService;
import vn.com.itechcorp.module.local.service.RisMessageService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.RISMessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.RISMessageFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RetrySendRisMessage {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Hl7MessageService hl7MessageService;

    private final RisMessageService risMessageService;

    private final ResendService resendService;

    @Value("${max.retry.send}")
    private String maxRetry;

    @Scheduled(fixedDelayString = "${time.delay.send.retry:10000}", initialDelay = 30000)
    public void reSendToRIS() throws Exception {
        List<RISMessageDTOGet> elements = this.risMessageService.getPageOfData(new RISMessageFilter(false, Integer.parseInt(maxRetry)),
                new PaginationInfo(0, 20)).getElements();
        logger.info("[RE-SEND][RIS] Found {} message need to sent", elements.size());
        if (elements.isEmpty()) return;
        for (RISMessageDTOGet el : elements) {
            // get hl7 from hl7_message
            Hl7MessageDTOGet messageIsNotSent = this.hl7MessageService.getById(el.getHl7MessageId());
            logger.info("[RIS-RowID-{}] resending to ris", messageIsNotSent.getId());

            this.resendService.resendToRis(messageIsNotSent.getId());
        }
    }
}
