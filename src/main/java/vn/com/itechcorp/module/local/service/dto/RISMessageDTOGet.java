package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.local.entity.RisMessage;

@Getter
@Setter
@NoArgsConstructor
public class RISMessageDTOGet extends DtoGet<RisMessage, Long> {
    private Long hl7MessageId;

    private String orderNumber;

    private String pid;

    private String accessionNumber;

    private String requestUrl;
    private String requestTime;
    private Integer requestCount;
    private boolean succeeded;
    private String errors;

    public RISMessageDTOGet(RisMessage risMessage) {
        super(risMessage);
    }

    @Override
    public void parse(RisMessage risMessage) {
        this.hl7MessageId = risMessage.getHl7MessageId();
        this.requestUrl = risMessage.getRequestUrl();
        this.requestTime = risMessage.getRequestTime();
        this.requestCount = risMessage.getRequestCount();
        this.succeeded = risMessage.isSucceeded();
        this.errors = risMessage.getErrors();
        this.orderNumber = risMessage.getOrderNumber();
        this.pid = risMessage.getPid();
        this.accessionNumber = risMessage.getAccessionNumber();
    }
}
