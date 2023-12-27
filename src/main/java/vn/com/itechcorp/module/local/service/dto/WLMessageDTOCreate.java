package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.base.util.AuditUtil;
import vn.com.itechcorp.module.local.entity.WorkListMessage;
import vn.com.itechcorp.util.DateUtil;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class WLMessageDTOCreate extends SerialIDDtoCreate<WorkListMessage> {
    private Long hl7MessageId;

    private String orderNumber;

    private String accessionNumber;

    private String requestUrl;

    private String requestTime;

    private Integer requestCount;

    private boolean succeeded;

    private String errors;

    public WLMessageDTOCreate(Long hl7MessageId, String requestUrl, Integer requestCount, boolean succeeded, String errors) {
        this.hl7MessageId = hl7MessageId;
        this.requestUrl = requestUrl;
        this.requestCount = requestCount;
        this.succeeded = succeeded;
        this.errors = errors;
    }

    @Override
    public WorkListMessage toEntry() {
        WorkListMessage workListMessage = new WorkListMessage();
        workListMessage.setHl7MessageId(this.hl7MessageId);
        workListMessage.setRequestUrl(this.requestUrl);
        workListMessage.setOrderNumber(this.orderNumber);
        workListMessage.setAccessionNumber(this.accessionNumber);
        workListMessage.setRequestTime(DateUtil.HIS_DATE_FORMAT.format(new Date()));
        workListMessage.setRequestCount(this.requestCount);
        workListMessage.setSucceeded(this.isSucceeded());
        workListMessage.setErrors(this.getErrors());
        return workListMessage;
    }
}
