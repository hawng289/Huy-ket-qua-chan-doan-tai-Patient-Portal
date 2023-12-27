package vn.com.itechcorp.module.notification.persistance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.BaseSerialIDEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "push_notification")
@Getter @Setter @NoArgsConstructor
public class Notification extends BaseSerialIDEntry {

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "pid")
    private String pid;

    @Column(name = "status")
    private boolean status;

}
