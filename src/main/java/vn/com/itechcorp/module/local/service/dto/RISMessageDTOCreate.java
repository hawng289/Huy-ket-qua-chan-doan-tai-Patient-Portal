package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.base.util.AuditUtil;
import vn.com.itechcorp.module.local.entity.RisMessage;
import vn.com.itechcorp.util.DateUtil;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RISMessageDTOCreate extends SerialIDDtoCreate<RisMessage> {
    private Long hl7MessageId;

    private String orderNumber;

    private String pid;

    private String accessionNumber;

    private String requestUrl;
    private String requestTime;
    private Integer requestCount;
    private boolean succeeded;
    private String errors;

    public RISMessageDTOCreate(Long hl7MessageId, String requestUrl, Integer requestCount, boolean succeeded, String errors) {
        this.hl7MessageId = hl7MessageId;
        this.requestUrl = requestUrl;
        this.requestCount = requestCount;
        this.succeeded = succeeded;
        this.errors = errors;
    }

    @Override
    public RisMessage toEntry() {
        RisMessage risMessage = new RisMessage();
        risMessage.setHl7MessageId(this.hl7MessageId);
        risMessage.setRequestUrl(this.requestUrl);
        risMessage.setOrderNumber(this.orderNumber);
        risMessage.setPid(this.pid);
        risMessage.setAccessionNumber(this.accessionNumber);
        risMessage.setRequestTime(DateUtil.HIS_DATE_FORMAT.format(new Date()));
        risMessage.setRequestCount(this.requestCount);
        risMessage.setSucceeded(this.isSucceeded());
        risMessage.setErrors(this.getErrors());
        return risMessage;
    }
}
