package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RisMWLResponse extends Dto {

    private boolean status;

    private Object data;

    public RisMWLResponse(boolean status) {
        this.status = status;
    }

    public RisMWLResponse(boolean status, Object data) {
        this.status = status;
        this.data = data;
    }
}
