package vn.com.itechcorp.module.lockorder.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.lockorder.persistance.LockOrder;

@Getter @Setter @NoArgsConstructor
public class LockOrderDTOGet extends DtoGet<LockOrder,Long> {

    private String orderNumber;

    private String pid;

    private String accessionNumber;

    private String request;

    private String response;

    public LockOrderDTOGet(LockOrder lockOrder) {
        super(lockOrder);
    }

    @Override
    public void parse(LockOrder lockOrder) {
        this.orderNumber = lockOrder.getOrderNumber();
        this.pid = lockOrder.getPid();
        this.accessionNumber = lockOrder.getAccessionNumber();
        this.request = lockOrder.getRequest();
        this.response = lockOrder.getResponse();
    }
}
