package vn.com.itechcorp.module.notification.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.module.notification.persistance.Notification;

@Getter @Setter @NoArgsConstructor
public class NotificationDTOCreate extends SerialIDDtoCreate<Notification> {

    private String accessionNumber;

    private String pid;

    private Boolean status;

    @Override
    public Notification toEntry() {
        Notification entry = new Notification();
        entry.setAccessionNumber(this.accessionNumber);
        entry.setPid(this.pid);
        entry.setStatus(status);

        return entry;
    }
}
