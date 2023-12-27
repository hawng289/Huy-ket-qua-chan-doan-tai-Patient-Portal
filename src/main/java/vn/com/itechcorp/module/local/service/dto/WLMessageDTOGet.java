package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.local.entity.WorkListMessage;

@Setter
@Getter
@NoArgsConstructor
public class WLMessageDTOGet extends DtoGet<WorkListMessage, Long> {
    private Long hl7MessageId;

    private String requestUrl;
    private String requestTime;
    private Integer requestCount;
    private boolean succeeded;
    private String errors;

    public WLMessageDTOGet(WorkListMessage workListMessage) {
        super(workListMessage);
    }

    @Override
    public void parse(WorkListMessage workListMessage) {
        this.hl7MessageId = workListMessage.getHl7MessageId();
        this.requestUrl = workListMessage.getRequestUrl();
        this.requestTime = workListMessage.getRequestTime();
        this.requestCount = workListMessage.getRequestCount();
        this.succeeded = workListMessage.isSucceeded();
        this.errors = workListMessage.getErrors();
    }
}
