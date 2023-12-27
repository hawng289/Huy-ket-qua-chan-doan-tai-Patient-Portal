package vn.com.itechcorp.module.local.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.AuditableSerialIDEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "hl7_message")
@Getter
@Setter
@NoArgsConstructor
public class Hl7Message extends AuditableSerialIDEntry {

    @Column(name = "hl7", nullable = false)
    private String hl7;

    @Column(name = "message_id")
    private String messageID;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "message_time", nullable = false)
    private Date messageTime;

}
