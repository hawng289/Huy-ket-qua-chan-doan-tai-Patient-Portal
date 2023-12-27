package vn.com.itechcorp.worklist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

@Setter
@NoArgsConstructor
@Getter
public class WorklistResponse extends Dto {
    private boolean succeed;
    private Object data;

    public WorklistResponse(boolean succeed) {
        this.succeed = succeed;
    }

    public WorklistResponse(boolean succeed, Object data) {
        this.succeed = succeed;
        this.data = data;
    }


    public String getData() {
        return data == null ? "" : data.toString();
    }

}
