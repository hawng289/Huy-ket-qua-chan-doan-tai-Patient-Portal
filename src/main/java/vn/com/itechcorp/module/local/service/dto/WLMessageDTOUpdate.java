package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoUpdate;
import vn.com.itechcorp.module.local.entity.WorkListMessage;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class WLMessageDTOUpdate extends DtoUpdate<WorkListMessage, Long> {

    private Integer requestCount;
    private boolean succeeded;
    private String errors;


    @Override
    public boolean apply(WorkListMessage workListMessage) {
        boolean modified = false;
        if (requestCount != null && !Objects.equals(requestCount, workListMessage.getRequestCount())) {
            workListMessage.setRequestCount(this.requestCount);
            modified = true;
        }
        if (succeeded != workListMessage.isSucceeded()) {
            workListMessage.setSucceeded(this.succeeded);
            modified = true;
        }
        if (errors != null && errors.equals(workListMessage.getErrors())) {
            workListMessage.setErrors(this.errors);
            modified = true;
        }
        return modified;
    }
}
