package vn.com.itechcorp.module.notification.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.notification.persistance.Notification;

@Getter @Setter @NoArgsConstructor
public class NotificationDTOGet extends DtoGet<Notification,Long> {

    private String accessionNumber;

    private String pid;

    private Boolean status;

    @Override
    public void parse(Notification notification) {
        this.setAccessionNumber(notification.getAccessionNumber());
        this.setPid(notification.getPid());
        this.setStatus(notification.isStatus());
    }

    public NotificationDTOGet(Notification notification) {
        super(notification);
    }
}
