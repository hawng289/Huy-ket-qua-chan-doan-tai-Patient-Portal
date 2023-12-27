package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoUpdate;
import vn.com.itechcorp.module.local.entity.RisMessage;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class RISMessageDTOUpdate extends DtoUpdate<RisMessage, Long> {
    private Integer requestCount;
    private boolean succeeded;
    private String errors;

    public RISMessageDTOUpdate(Integer requestCount, boolean succeeded, String errors) {
        this.requestCount = requestCount;
        this.succeeded = succeeded;
        this.errors = errors;
    }

    @Override
    public boolean apply(RisMessage risMessage) {
        boolean modified = false;
        if (requestCount != null && !Objects.equals(requestCount, risMessage.getRequestCount())) {
            risMessage.setRequestCount(this.requestCount);
            modified = true;
        }
        if (succeeded != risMessage.isSucceeded()) {
            risMessage.setSucceeded(this.succeeded);
            modified = true;
        }
        if (errors != null && errors.equals(risMessage.getErrors())) {
            risMessage.setErrors(this.errors);
            modified = true;
        }
        return modified;
    }
}
