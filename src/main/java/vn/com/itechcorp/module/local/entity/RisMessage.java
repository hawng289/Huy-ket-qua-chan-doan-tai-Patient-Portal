package vn.com.itechcorp.module.local.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.AuditableSerialIDEntry;

import javax.persistence.*;

@Entity
@Table(name = "ris_message")
@Setter
@Getter
@NoArgsConstructor
public class RisMessage extends AuditableSerialIDEntry {
    @Column(name = "hl7_message_id", nullable = false)
    private Long hl7MessageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hl7_message_id", insertable = false, updatable = false)
    private Hl7Message hl7Message;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "pid")
    private String pid;

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "request_url")
    private String requestUrl;
    @Column(name = "request_time")
    private String requestTime;
    @Column(name = "request_count")
    private Integer requestCount;
    @Column(name = "succeeded")
    private boolean succeeded;
    @Column(name = "errors")
    private String errors;
}
