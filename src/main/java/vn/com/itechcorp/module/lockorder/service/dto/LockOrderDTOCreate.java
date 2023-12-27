package vn.com.itechcorp.module.lockorder.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.module.lockorder.persistance.LockOrder;

@Getter @Setter @NoArgsConstructor
public class LockOrderDTOCreate extends SerialIDDtoCreate<LockOrder> {

    private String orderNumber;

    private String pid;

    private String accessionNumber;

    private String request;

    private String response;

    @Override
    public LockOrder toEntry() {
        LockOrder entry = new LockOrder();
        entry.setOrderNumber(orderNumber);
        entry.setPid(pid);
        entry.setAccessionNumber(accessionNumber);
        entry.setRequest(request);
        entry.setResponse(response);
        return entry;
    }
}
